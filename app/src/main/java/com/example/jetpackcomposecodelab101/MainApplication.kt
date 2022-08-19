package com.example.jetpackcomposecodelab101

import android.R.attr.bitmap
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.launchdarkly.sdk.LDUser
import com.launchdarkly.sdk.LDValue
import com.launchdarkly.sdk.android.ConnectionInformation
import com.launchdarkly.sdk.android.LDClient
import com.launchdarkly.sdk.android.LDConfig
import com.launchdarkly.sdk.android.LDFailure
import com.launchdarkly.sdk.android.LDStatusListener
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainApplication : Application() {

    private lateinit var ldClient: LDClient
    private lateinit var ldConfig: LDConfig
    private lateinit var ldUser: LDUser

    override fun onCreate() {
        super.onCreate()

        ldConfig = LDConfig.Builder()
            //Coach Roebuck's account...
            // Test
            .mobileKey("mob-c448398a-104d-4e9a-a829-f6e0ef245989")
            // Prod
            // .mobileKey("mob-81151918-ec0c-4163-b56a-7957c6286528")
            .evaluationReasons(true)
            .inlineUsersInEvents(true)
            .disableBackgroundUpdating(true)
            .build()

        val firstName = "Polenta"
        val lastName = "Drea"
        val avatar = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaBJsvhB9uNRXbwnd5kBReTmaGKi9dciJzog&usqp=CAU"
        ldUser = LDUser.Builder("user-$firstName-$lastName")
            .secondary("secondary-user-$firstName-$lastName")
            .email("$firstName.$lastName@email.com")
            .firstName(firstName)
            .lastName(lastName)
            .country(Locale.getDefault().country)
            .ip("${Random.nextInt(255)}" +
                ".${Random.nextInt(255)}" +
                ".${Random.nextInt(255)}" +
                ".${Random.nextInt(255)}")
            .name("$firstName $lastName")
            .avatar(avatar)
            .anonymous(false)
            .custom("isSpecialUser", LDValue.buildArray().add(Random.nextBoolean()).build())
            .build()

        // Create a new LDClient instance with your environment-specific mobile SDK key
        // Create a new LDClient instance with your environment-specific mobile SDK key
        ldClient = LDClient.init(this, ldConfig, ldUser, 5)

        val isInitialized = ldClient.isInitialized
        val isDisableBackgroundPolling = ldClient.isDisableBackgroundPolling
        val isOffline = ldClient.isOffline
        ldClient.registerFeatureFlagListener("isBiometricEnabled") {
            println("LaunchDarkly isBiometricEnabled Flag=[$it]")
        }
        ldClient.registerAllFlagsListener {
            println("LaunchDarkly allFlags=[$it]")
        }
        ldClient.registerStatusListener(object : LDStatusListener {
            override fun onConnectionModeChanged(connectionInformation: ConnectionInformation?) {
                println("LaunchDarkly onConnectionModeChanged() connectionInformation=[$connectionInformation]")
            }

            override fun onInternalFailure(ldFailure: LDFailure?) {
                println("LaunchDarkly onInternalFailure() ldFailure=[$ldFailure]")
            }
        })
        println("LaunchDarkly isInitialized=[$isInitialized]")
        println("LaunchDarkly isDisableBackgroundPolling=[$isDisableBackgroundPolling]")
        println("LaunchDarkly isOffline=[$isOffline]")
        ldClient.track("Tracked Event")
        ldClient.trackData("New Data", LDValue.buildArray().add("$firstName $lastName").build())
        ldClient.trackMetric("New Metric", LDValue.buildArray().add("$firstName $lastName").build(), 1.0)
    }

    val isBiometricEnabled: Boolean get() {
         return ldClient.boolVariation("isBiometricEnabled", false)
    }

    override fun onTerminate() {
        ldClient.close()
        super.onTerminate()
    }
}
