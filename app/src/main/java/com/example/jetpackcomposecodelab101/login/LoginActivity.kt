package com.example.jetpackcomposecodelab101.login

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationError
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.arkivanov.mvikotlin.core.lifecycle.asMviLifecycle
import com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
import com.arkivanov.mvikotlin.keepers.instancekeeper.getInstanceKeeper
import com.arkivanov.mvikotlin.keepers.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.mvikotlin.keepers.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.keepers.statekeeper.get
import com.arkivanov.mvikotlin.keepers.statekeeper.getSerializableStateKeeperRegistry
import com.example.jetpackcomposecodelab101.R
import com.example.jetpackcomposecodelab101.base.DefaultAppActivity
import com.example.jetpackcomposecodelab101.base.launchDashboard
import com.example.jetpackcomposecodelab101.shared.DefaultLoginCryptoObject
import com.example.jetpackcomposecodelab101.shared.EncryptedMessage
import com.example.jetpackcomposecodelab101.ui.DefaultAppThemeState
import com.example.jetpackcomposecodelab101.ui.theme.ColorPalette
import com.example.jetpackcomposecodelab101.ui.theme.JetpackComposeCodeLab101Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@DelicateCoroutinesApi
@ExperimentalInstanceKeeperApi
@ExperimentalStateKeeperApi
class LoginActivity : DefaultAppActivity() {

    // region Properties

    private lateinit var viewModel: MutableState<DefaultLoginViewModel>
    private lateinit var controller: LoginController
    private lateinit var coroutineScope: CoroutineScope
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback =
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                @AuthenticationError errorCode: Int, errString: CharSequence,
            ) {
                super.onAuthenticationError(errorCode, errString)

                val message = "${getString(R.string.biometrics_authentication_error)}: " +
                        "($errorCode) $errString"
                showToastMessage(message)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                showToastMessage(R.string.biometrics_authentication_succeeded)
                encryptPassword(result)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                showToastMessage(R.string.biometrics_authentication_failed)
            }
        }

    // endregion

    @ExperimentalStateKeeperApi
    companion object {
        private var stateKeeper: StateKeeper<LoginStore.State>? = null
    }

    // region Override Methods

    override fun onDestroy() {
        super.onDestroy()
        stateKeeper?.unregister()
    }

    @Composable
    override fun MyApp(savedInstanceState: Bundle?) {
//        val viewModel: LoginViewModel by viewModels { AppViewModelFactory(this) }

        InitViewModel()
        coroutineScope = rememberCoroutineScope()
        CreateMviKotlinController()
        DefaultView()
    }

    // endregion

    // region Private Methods

    @Composable
    private fun InitViewModel() {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val currentState: MutableState<LoginStore.State> =
            remember { mutableStateOf(LoginStore.State.Idle) }
        val isUserNameEnabled = rememberSaveable { mutableStateOf(true) }
        val userNameShowError = rememberSaveable { mutableStateOf(false) }
        val userName = rememberSaveable { mutableStateOf("") }
        val userNameFocusRequester = remember { FocusRequester() }
        val password = rememberSaveable { mutableStateOf("") }
        val bioMetricsPassword : MutableState<EncryptedMessage?> = rememberSaveable { mutableStateOf(null) }
        val isPasswordVisible = remember { mutableStateOf(false) }
        val isPasswordEnabled = remember { mutableStateOf(false) }
        val passwordShowError = remember { mutableStateOf(false) }
        val passwordFocusRequester = remember { FocusRequester() }
        val responseText = rememberSaveable { mutableStateOf("") }
        val isLoginEnabled = rememberSaveable { mutableStateOf(false) }
        val isBioMetricsEnabled = rememberSaveable { mutableStateOf(false) }

        viewModel = remember {
            mutableStateOf(DefaultLoginViewModel(
                isPasswordVisible = isPasswordVisible,
                isPasswordEnabled = isPasswordEnabled,
                passwordShowError = passwordShowError,
                password = password,
                bioMetricsPassword = bioMetricsPassword,
                passwordFocusRequester = passwordFocusRequester,
                responseText = responseText,
                isLoginEnabled = isLoginEnabled,
                isUserNameEnabled = isUserNameEnabled,
                userNameShowError = userNameShowError,
                userName = userName,
                userNameFocusRequester = userNameFocusRequester,
                focusManager = focusManager,
                keyboardController = keyboardController,
                currentState = currentState,
                isBioMetricsEnabled = isBioMetricsEnabled,
                loginCrypto = DefaultLoginCryptoObject()
            ))
        }
    }

    @Composable
    private fun CreateMviKotlinController() {
        val instanceKeeper = getInstanceKeeper()
        val stateKeeperRegistry = getSerializableStateKeeperRegistry()

        if (stateKeeper == null) {
            stateKeeper = stateKeeperRegistry.get()
        }

        controller = DefaultLoginController(
            viewModel.value,
            lifecycle.asMviLifecycle(),
            instanceKeeper,
            stateKeeper
        )

        LaunchedEffect(Unit) {
            controller.stateFlow.collect(object : FlowCollector<LoginStore.State> {
                override suspend fun emit(value: LoginStore.State) {
                    onStateReceived(value)
                }
            })
        }

        DisposableEffect(lifecycle) {
            // Create an observer that triggers our remembered callbacks
            // for sending analytics events
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    println("event=[$event]")
                } else if (event == Lifecycle.Event.ON_STOP) {
                    println("event=[$event]")
                }
            }

            // Add the observer to the lifecycle
            lifecycle.addObserver(observer)

            // When the effect leaves the Composition, remove the observer
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
        SideEffect {

        }
    }

    @Composable
    private fun DefaultView() {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.login_text),
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                )
                CreateUserNameField()
                Divider(color = Color.LightGray)
                CreatePasswordField()
                if (isBiometricReady(this@LoginActivity)) {
                    CreateBioMetricsField()
                }
                CreateLoginButtonField()
                CreateForgotPasswordField()
                CreateSignUpField()
            }
        }
    }

    // region Jetpack Compose UI Components

    @Composable
    private fun CreateUserNameField() {
        viewModel.value.apply {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(userNameFocusRequester),
                enabled = isUserNameEnabled.value,
                value = userName.value,
                onValueChange = {
                    controller.emit(LoginStore.Intent.UserNameProvided.values(it))
                },
                placeholder = {
                    Text(text = stringResource(R.string.enter_user_name_text))
                },
                label = { Text(stringResource(R.string.user_name_text)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        controller.emit(LoginStore.Intent.FinishedProvidingUserName)
                    }
                ),
                singleLine = true,
                isError = userNameShowError.value,
                visualTransformation = VisualTransformation.None,
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
//        DisposableEffect(Unit) {
//            viewModel.userNameFocusRequester.requestFocus()
//            viewModel.keyboardController?.show()
//            onDispose { }
//        }
        }
    }

    @Composable
    private fun CreatePasswordField() {
        viewModel.value.apply {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                enabled = isPasswordEnabled.value,
                value = password.value,
                onValueChange = {
                    controller.emit(LoginStore.Intent.PasswordProvided.values(it))
                },
                placeholder = {
                    Text(text = stringResource(R.string.enter_password_text))
                },
                label = { Text(stringResource(R.string.password_text)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = true,
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = { requestLogin() }
                ),
                singleLine = true,
                isError = passwordShowError.value,
                visualTransformation =
                if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val (icon, iconColor) = if (isPasswordVisible.value) {
                        Pair(Icons.Filled.Visibility, MaterialTheme.colors.secondary)
                    } else {
                        Pair(Icons.Filled.VisibilityOff, MaterialTheme.colors.secondaryVariant)
                    }

                    IconButton(onClick = {
                        isPasswordVisible.value = !isPasswordVisible.value
                    }) {
                        Icon(
                            icon,
                            contentDescription = if (isPasswordVisible.value) {
                                stringResource(R.string.password_visible_text)
                            } else {
                                stringResource(R.string.password_invisible_text)
                            },
                            tint = iconColor
                        )
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
        }
    }

    @Composable
    private fun CreateBioMetricsField() {
        viewModel.value.apply {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(id = R.string.enable_biometrics_prompt)
                )
                Switch(
                    modifier = Modifier.padding(vertical = 24.dp),
                    enabled = isLoginEnabled.value,
                    checked = isBioMetricsEnabled.value,
                    onCheckedChange = {
                        controller.emit(LoginStore.Intent.BioMetricsChanged.values(
                            !isBioMetricsEnabled.value
                        ))
                    }
                )
            }
        }
    }

    @Composable
    private fun CreateLoginButtonField() {
        viewModel.value.apply {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when {
                    userNameShowError.value -> {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = MaterialTheme.colors.error,
                            text = stringResource(id = R.string.user_name_invalid_text)
                        )
                    }
                    passwordShowError.value -> {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = MaterialTheme.colors.error,
                            text = stringResource(id = R.string.password_invalid_text)
                        )
                    }
                    currentState.value is LoginStore.State.ApiError -> {
                        val message =
                            (currentState.value as LoginStore.State.ApiError).throwable?.message
                                ?: stringResource(
                                    id = R.string.internal_server_error
                                )
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = MaterialTheme.colors.error,
                            text = message
                        )
                    }
                    currentState.value is LoginStore.State.Error -> {
                        val message =
                            (currentState.value as LoginStore.State.Error).throwable?.message
                                ?: stringResource(
                                    id = R.string.internal_server_error
                                )
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = MaterialTheme.colors.error,
                            text = message
                        )
                    }
                    currentState.value is LoginStore.State.LoginUiState
                            && (currentState.value as LoginStore.State.LoginUiState).throwable != null -> {
                        val loginUiState = (currentState.value as LoginStore.State.LoginUiState)
                        if (loginUiState.throwable != null) {
                            val message =
                                (currentState.value as LoginStore.State.LoginUiState).throwable?.message
                                    ?: stringResource(
                                        id = R.string.internal_server_error
                                    )
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = MaterialTheme.colors.error,
                                text = message
                            )
                        } else if (loginUiState.shouldShowProgress) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = stringResource(id = R.string.logging_in_text)
                            )
                            CreateCircularProgressIndicator(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                progress = 0f
                            )
                        }
                    }
                }
                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    enabled = isLoginEnabled.value,
                    onClick = {
                        requestLogin()
                    }
                ) {
                    Text(stringResource(R.string.login_text))
                }
            }

        }
    }

    private fun DefaultLoginViewModel.requestLogin() {
        controller.emit(LoginStore.Intent.LoginRequested.values(
            userName.value,
            password.value
        ))
    }

    @Composable
    private fun CreateCircularProgressIndicator(
        modifier: Modifier = Modifier,
        progress: Float = 0.0f,
        color: Color = MaterialTheme.colors.primary,
        strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val progressAnimationValue by infiniteTransition.animateFloat(
            initialValue = progress,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(animation = tween(900))
        )

        CircularProgressIndicator(
            modifier = modifier,
            progress = progressAnimationValue,
            color = color,
            strokeWidth = strokeWidth
        )
    }

    @Composable
    private fun CreateForgotPasswordField() {
        viewModel.value.apply {
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                enabled = currentState.value != LoginStore.State.LoginAttemptInProgress,
                onClick = { }
            ) {
                Text(stringResource(R.string.forgot_password_text))
            }
        }
    }

    @Composable
    private fun CreateSignUpField() {
        viewModel.value.apply {
            Text(stringResource(R.string.dont_have_an_account))
            Text(
                modifier = Modifier.clickable {
                    if (currentState.value != LoginStore.State.LoginAttemptInProgress) {
                        println("Sign Up Clicked")
                    }
                },
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary
                ),
                text = stringResource(R.string.create_account_text)
            )
        }
    }

    // endregion

    private fun onStateReceived(value: LoginStore.State) {
        println("LoginActivity: New State Received=[$value]")
        when (value) {
            LoginStore.State.ApiError -> {}
            LoginStore.State.CanLogin -> {}
            LoginStore.State.CanProvidePassword -> {}
            LoginStore.State.Error -> {}
            LoginStore.State.FocusOnPassword -> onFocusOnPassword()
            LoginStore.State.FocusOnUserName -> onFocusOnUserName()
            LoginStore.State.Idle -> {}
            LoginStore.State.LoginAttemptInProgress -> {}
            LoginStore.State.PasswordInProgress -> {}
            LoginStore.State.PasswordInvalid -> {}
            LoginStore.State.UserNameInProgress -> {}
            LoginStore.State.UserNameInvalid -> {}
            LoginStore.State.AccessToken,
            LoginStore.State.LaunchDashboard,
            -> this.launchDashboard()
            is LoginStore.State.LoginUiState -> onLoginUiState(value)
            LoginStore.State.PromptForBioMetric -> launchBiometrics()
        }
    }

    private fun onLoginUiState(state: LoginStore.State.LoginUiState) {
        if (state.canShowDashboard) {
            this.launchDashboard()
        } else {
            viewModel.value.apply {
                isLoginEnabled.value = state.isLoginEnabled
                isPasswordEnabled.value = state.isPasswordEnabled
                isUserNameEnabled.value = state.isUserNameEnabled
                userNameShowError.value = state.userNameShowError
                passwordShowError.value = state.passwordShowError
                state.userName?.let { userName.value = it }
                state.password?.let { password.value = it }
                if (state.shouldHideKeyboard) {
                    keyboardController?.hide()
                }
            }
        }
    }

    private fun onFocusOnPassword() {
        viewModel.value.apply {
            focusManager.clearFocus(force = true)
            passwordFocusRequester.requestFocus()
        }
    }

    private fun onFocusOnUserName() {
        viewModel.value.apply {
            focusManager.clearFocus(force = true)
            userNameFocusRequester.requestFocus()
        }
    }

    private fun encryptPassword(result: BiometricPrompt.AuthenticationResult) {
        viewModel.value.apply {
            if (password.value.isNotEmpty()) {
                val password = password.value
                val cipher = result.cryptoObject?.cipher
                controller.emit(LoginStore.Intent.EncryptPassword(
                    this@LoginActivity,
                    password,
                    cipher
                ))
            }
        }
    }

    private fun showToastMessage(resourceId: Int, vararg args: String) {
        showToastMessage(getString(resourceId, args))
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isBiometricsSupported(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isDeviceSecure) {
            return true
        } else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }

    private fun launchBiometrics() {
        viewModel.value.keyboardController?.hide()
        if (isBiometricsSupported()) {
            val executor = ContextCompat.getMainExecutor(this)

            val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
                .apply {
                    setTitle(getString(R.string.prompt_info_title))
                    setSubtitle(getString(R.string.prompt_info_subtitle))
                    setDescription(getString(R.string.prompt_info_description))
                    setConfirmationRequired(false)
                    setAllowedAuthenticators(BIOMETRIC_STRONG)
                    setNegativeButtonText(getString(R.string.prompt_info_use_app_password))
                }.build()
//            biometricPrompt.authenticate(getCancellationSignal())
            val biometricPrompt = BiometricPrompt(this, executor, authenticationCallback)
            biometricPrompt.apply {
                viewModel.value.encryptionObject.let {
                    authenticate(biometricPromptInfo, it)
                }
            }
        }
    }

    private fun isBiometricReady(context: Context) =
        hasBiometricCapability(context) == BiometricManager.BIOMETRIC_SUCCESS

    private fun hasBiometricCapability(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BIOMETRIC_STRONG)
    }

    // endregion

    // region Compose Preview

    @Preview(
        fontScale = 1f,
        name = "Light Mode",
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        showSystemUi = true,
        showBackground = true
    )
    @Preview(
        fontScale = 1f,
        name = "Dark Mode",
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        showSystemUi = true,
        showBackground = true
    )
    @Composable
    fun Preview() {
        val systemUiController = rememberSystemUiController()
        val isDarkMode = isSystemInDarkTheme()
        val appThemeState =
            remember {
                mutableStateOf(
                    DefaultAppThemeState(
                        isSystemModeEnable = true,
                        isDarkTheme = isDarkMode,
                        colorPalette = ColorPalette.Orenji
                    )
                )
            }
        InitViewModel()
        JetpackComposeCodeLab101Theme(
            systemUiController = systemUiController,
            appThemeState = appThemeState.value,
        ) {
            DefaultView()
        }
    }

    // endregion
}

