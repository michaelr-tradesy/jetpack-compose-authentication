package com.example.jetpackcomposecodelab101.google

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import com.example.jetpackcomposecodelab101.DefaultLogAdapter
import com.example.jetpackcomposecodelab101.LogAdapter
import com.example.jetpackcomposecodelab101.PreferencesAdapter
import com.example.jetpackcomposecodelab101.login.LoginStore
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.ConnectionResult.*
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices

interface GoogleCredentialsAdapter {
    fun onRestoreInstanceState(savedInstanceState: Bundle?)
    fun onSaveInstanceState(outState: Bundle?)
    fun requestCredentials(context: Context, callback: (LoginStore.Result) -> Unit)
    fun saveCredentials(
        context: Context,
        email: String,
        password: String,
        callback: (LoginStore.Result) -> Unit
    )

    fun deleteCredentials(
        context: Context,
        userName: String,
        password: String?,
        callback: (LoginStore.Result) -> Unit
    )

    fun getLastLocation(context: Context, callback: (LoginStore.Result) -> Unit)
    fun onRequestPermissionsResult(
        context: Context,
        requestCode: Int,
        grantResults: IntArray,
        callback: (LoginStore.Result) -> Unit
    )

    fun startLocationPermissionRequest(context: Context)
    fun unbind()
    fun onActivityResult(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: (LoginStore.Result) -> Unit
    )

    fun disableAutoSignIn()
}

class DefaultGoogleCredentialsAdapter(
    private val preferencesAdapter: PreferencesAdapter,
    private val logAdapter: LogAdapter = DefaultLogAdapter(),
    private val isAutoManage: Boolean = false
) : GoogleCredentialsAdapter {

    // region Properties

    var didReceiveError = false

    private var googleApiClient: GoogleApiClient? = null
    private var mIsResolving: Boolean = false
    private var mIsRequesting: Boolean = false
    private var didRequestCredentials = false

    companion object {
        const val googleCredentialsKey = 1000
        private const val saveKey = googleCredentialsKey + 1
        private const val gpsRequiredCode = googleCredentialsKey + 2
        private const val readKey = googleCredentialsKey + 3
        private const val requestPermissionKey = googleCredentialsKey + 4
        private const val deleteKey = googleCredentialsKey + 5
        private const val isResolvingKey = "is_resolving"
        private const val isRequestingKey = "is_requesting"
    }

    // endregion

    // region Override Methods

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            mIsResolving = it.getBoolean(isResolvingKey)
            mIsRequesting = it.getBoolean(isRequestingKey)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.let {
            it.putBoolean(isResolvingKey, mIsResolving)
            it.putBoolean(isRequestingKey, mIsRequesting)
        }
    }

    override fun disableAutoSignIn() {
        googleApiClient?.let { Auth.CredentialsApi.disableAutoSignIn(it) }
    }

    override fun requestCredentials(context: Context, callback: (LoginStore.Result) -> Unit) {
        if (canRequestCredentials(context)) {
            didRequestCredentials = true
            connectToApiClient(
                context,
                readKey,
                { requestCredentialsAfterApiClientConnection(context, callback) },
                { unbind() },
                { unbind() }
            )
        }
    }

    override fun saveCredentials(
        context: Context,
        email: String,
        password: String,
        callback: (LoginStore.Result) -> Unit
    ) {
        if (canSaveCredentials(context)) {
            connectToApiClient(
                context,
                saveKey,
                { saveCredentialsAfterConnectingToApiClient(context, email, password, callback) },
                { unbind() },
                { unbind() }
            )
        } else {
            callback(LoginStore.Result.GoogleCredentialsSaved)
            unbind()
        }
    }

    override fun deleteCredentials(
        context: Context,
        userName: String,
        password: String?,
        callback: (LoginStore.Result) -> Unit
    ) {
        if (canDeleteCredentials(context)) {
            connectToApiClient(
                context,
                deleteKey,
                { deleteCredentialsAfterConnection(context, userName, password, callback) },
                { unbind() },
                { unbind() }
            )
        }
    }

    override fun onActivityResult(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: (LoginStore.Result) -> Unit
    ) {
        this.logAdapter.d("onActivityResult:$requestCode:$resultCode:$data")

        if (requestCode == readKey) {
            if (resultCode == RESULT_OK) {
                val credential = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                processRetrievedCredential(credential, callback)
            } else {
                this.logAdapter.e("Credential Read: NOT OK")
            }
        } else if (requestCode == saveKey) {
            this.logAdapter.d("Result code: $resultCode")
            if (resultCode == RESULT_OK) {
                this.logAdapter.d("Credential Save: OK")
            } else {
                this.logAdapter.e("Credential Save Failed")
            }
            callback(LoginStore.Result.GoogleCredentialsSaved)
            unbind()
        } else if (requestCode == requestPermissionKey) {
            // Ignore. We are handling this situation at another location.
        } else if (requestCode == gpsRequiredCode) {
            getLastLocation(context, callback)
        }
        mIsResolving = false
    }

    override fun unbind() {
        mIsResolving = false
        mIsRequesting = false
    }

    override fun startLocationPermissionRequest(context: Context) {
        if (isGooglePlayServicesAvailable(context)) {
            ActivityCompat.requestPermissions(
                context as ComponentActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), requestPermissionKey
            )
        }
    }

    override fun onRequestPermissionsResult(
        context: Context,
        requestCode: Int,
        grantResults: IntArray,
        callback: (LoginStore.Result) -> Unit
    ) {
        if (requestCode == gpsRequiredCode) {
            when {
                grantResults.isEmpty() -> this.logAdapter.i("User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> getLastLocation(
                    context,
                    callback
                )
                else -> callback(LoginStore.Result.GooglePermissionDenied)
            }
        }
    }

    override fun getLastLocation(
        context: Context,
        callback: (LoginStore.Result) -> Unit
    ) {
        if (isGooglePlayServicesAvailable(context)) {
            try {
                val baseActivity = context as ComponentActivity
                val mFusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(baseActivity)

                mFusedLocationClient.lastLocation
                    .addOnFailureListener { p0 ->
                        if (p0 is SecurityException) {
                            requestPermissions(context, callback)
                        }
                        p0.message?.let {
                            logAdapter.e(it)
                        }
                    }
                    .addOnSuccessListener { location ->
                        location?.let {
                            val intent = Intent()
                            intent.putExtra("Longitude", it.longitude)
                            intent.putExtra("Latitude", it.latitude)
                            preferencesAdapter.latitude = it.latitude.toFloat()
                            preferencesAdapter.longitude = it.longitude.toFloat()
                            baseActivity.setResult(gpsRequiredCode, intent)
                        }
                        if (location == null) {
                            callback(LoginStore.Result.GoogleDownloadLocation)
                        }
                    }
            } catch (e: SecurityException) {
                e.message?.let {
                    this.logAdapter.e(it)
                }
            }
        }
    }

    // endregion

    // region Private Methods

    private fun connectToApiClient(
        context: Context,
        key: Int,
        postConnect: () -> Unit,
        onSuspended: () -> Unit,
        onError: () -> Unit
    ) {
        val googleCallback = object : GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {
            override fun onConnected(p0: Bundle?) {
                postConnect()
            }

            override fun onConnectionSuspended(p0: Int) {
                onSuspended()
                didReceiveError = true
            }

            override fun onConnectionFailed(connectionResult: ConnectionResult) {
                logAdapter.d(
                    "FAILED to connect to Google Play Services Suspended. Error=["
                            + connectionResult + "]"
                )
                onError()
                didReceiveError = true
            }

        }
        buildGoogleApiClient(context, key, googleCallback, googleCallback)
    }

    private fun canRequestCredentials(context: Context) =
        !mIsRequesting && !didRequestCredentials && !didReceiveError && isGooglePlayServicesAvailable(
            context
        )

    private fun requestCredentialsAfterApiClientConnection(
        context: Context,
        callback: (LoginStore.Result) -> Unit
    ) {
        mIsRequesting = true

        val request = CredentialRequest.Builder()
            .setPasswordLoginSupported(true)
            .build()

        googleApiClient?.let {
            Auth.CredentialsApi.request(it, request)
                .setResultCallback { credentialRequestResult ->
                    disposeGoogleApiClient(context)
                    val status = credentialRequestResult.status
                    when {
                        credentialRequestResult.status.isSuccess -> {
                            // Successfully read the credential without any user interaction, this
                            // means there was only a single credential and the user has auto
                            // sign-in enabled.
                            val credential = credentialRequestResult.credential
                            processRetrievedCredential(credential, callback)
                        }
                        status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED -> {
                            // This is most likely the case where the user has multiple saved
                            // credentials and needs to pick one.
                            resolveResult(context, status, readKey, callback)
                        }
                        status.statusCode == CommonStatusCodes.SIGN_IN_REQUIRED -> {
                            // This is most likely the case where the user does not currently
                            // have any saved credentials and thus needs to provide a username
                            // and password to sign in.
                            logAdapter.d("Sign in required")
                        }
                        else -> {
                            logAdapter.w("Unrecognized status code: " + status.statusCode)
                        }
                    }
                }
        }
    }

    private fun processRetrievedCredential(
        credential: Credential?,
        callback: (LoginStore.Result) -> Unit
    ) {
        credential?.let {
            callback(LoginStore.Result.GoogleCredentialsReceived.values(it.id, it.password))
            unbind()
        }
    }

    private fun canSaveCredentials(context: Context) =
        !mIsRequesting && !didReceiveError && isGooglePlayServicesAvailable(context)

    private fun saveCredentialsAfterConnectingToApiClient(
        context: Context,
        userName: String,
        password: String,
        callback: (LoginStore.Result) -> Unit
    ) {

        mIsRequesting = true

        val credential = Credential.Builder(userName)
            .setPassword(password)
            .build()

        googleApiClient?.let {
            Auth.CredentialsApi.save(it, credential).setResultCallback { status ->
                disposeGoogleApiClient(context)
                if (status.isSuccess) {
                    this.logAdapter.d("User credentials saved to Google Play Services...")
                    callback(LoginStore.Result.GoogleCredentialsSaved)
                    unbind()
                } else {
                    resolveResult(context, status, saveKey, callback)
                }
            }
        }
    }

    private fun canDeleteCredentials(context: Context) =
        !mIsRequesting && isGooglePlayServicesAvailable(context)

    private fun deleteCredentialsAfterConnection(
        context: Context,
        userName: String,
        password: String?,
        callback: (LoginStore.Result) -> Unit
    ) {

        mIsRequesting = true

        val credential = Credential.Builder(userName)
            .setPassword(password)
            .build()

        googleApiClient?.let {
            Auth.CredentialsApi.delete(
                it,
                credential
            ).setResultCallback { status ->
                disposeGoogleApiClient(context)
                if (status.isSuccess) {
                    this.logAdapter.d("Credential successfully deleted.")
                    callback(LoginStore.Result.GoogleCredentialsDeleted)
                    unbind()
                } else {
                    // This may be due to the credential not existing, possibly
                    // already deleted via another device/app.
                    this.logAdapter.d("Credential not deleted successfully.")
                    callback(LoginStore.Result.GoogleCredentialsError)
                    unbind()
                }
            }
        }
    }

    //TODO: Extract the singleton
    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val gms = GoogleApiAvailability.getInstance()
        return when (gms.isGooglePlayServicesAvailable(context)) {
            SUCCESS -> true
            SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED -> {
                //Don't notify the end user.
                // The user might not be able to install Google Play Services.
//                val baseActivity = context as ComponentActivity
//                if(!baseActivity.isDestroyed) {
//                    gms.getErrorDialog(baseActivity, connectionResult, gpsRequiredCode).show()
//                }
                didReceiveError = true
                false
            }
            else -> {
                didReceiveError = true
                false
            }
        }
    }

    private fun resolveResult(
        context: Context,
        status: Status,
        requestCode: Int,
        callback: (LoginStore.Result) -> Unit
    ) {
        // We don't want to fire multiple resolutions at once since that
        // can   result in stacked dialogs after rotation or another
        // similar event.
        if (mIsResolving) {
            this.logAdapter.w("resolveResult: already resolving.")
            return
        }

        this.logAdapter.d("Resolving: $status")
        if (status.hasResolution()) {
            this.logAdapter.d("STATUS: RESOLVING")
            try {
                (context as ComponentActivity?)?.let { status.startResolutionForResult(it, requestCode) }
                mIsResolving = true
            } catch (e: IntentSender.SendIntentException) {
                this.logAdapter.e("STATUS: Failed to send resolution. " + e.message)
                if (requestCode == saveKey) {
                    callback(LoginStore.Result.GoogleCredentialsSaved)
                    unbind()
                }
            }

        } else {
            this.logAdapter.e("STATUS: FAIL")
            if (requestCode == saveKey) {
                callback(LoginStore.Result.GoogleCredentialsSaved)
                unbind()
            }
        }
    }

    private fun buildGoogleApiClient(
        context: Context,
        requestCode: Int,
        connectionCallback: GoogleApiClient.ConnectionCallbacks,
        failureCallback: GoogleApiClient.OnConnectionFailedListener?
    ) {
        disposeGoogleApiClient(context)
        val googleApiClientBuilder = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(connectionCallback)
            .addApi(Auth.CREDENTIALS_API)

        if (isAutoManage) {
            // TODO: This calls for a fragment activity. What should I do?
//            googleApiClientBuilder.enableAutoManage(context as FragmentActivity, requestCode, failureCallback)
        }
        googleApiClient = googleApiClientBuilder.build()
    }

    private fun disposeGoogleApiClient(context: Context) {
        mIsResolving = false
        mIsRequesting = false
        if (googleApiClient != null) {
            if(isAutoManage) {
                // TODO: This is a fragment activity. What am I to do?
//                googleApiClient?.stopAutoManage(context as FragmentActivity)
            }
            googleApiClient?.disconnect()
            googleApiClient = null
        }
    }

    @Suppress("unused")
    private fun checkPermissions(context: Context): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            context as ComponentActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(
        context: Context,
        callback: (LoginStore.Result) -> Unit
    ) {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            context as ComponentActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            callback(LoginStore.Result.GooglePermissionsRequested)
            this.logAdapter.i("Displaying permission rationale to provide additional context.")

        } else {
            this.logAdapter.i("Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest(context)
        }
    }

    // endregion
}
