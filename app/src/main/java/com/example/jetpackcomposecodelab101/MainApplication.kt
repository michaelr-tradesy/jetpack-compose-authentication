package com.example.jetpackcomposecodelab101

import android.app.Application
import com.launchdarkly.sdk.LDUser
import com.launchdarkly.sdk.LDValue
import com.launchdarkly.sdk.android.ConnectionInformation
import com.launchdarkly.sdk.android.FeatureFlagChangeListener
import com.launchdarkly.sdk.android.LDAllFlagsListener
import com.launchdarkly.sdk.android.LDClient
import com.launchdarkly.sdk.android.LDConfig
import com.launchdarkly.sdk.android.LDFailure
import com.launchdarkly.sdk.android.LDStatusListener
import java.util.Locale
import kotlin.random.Random

class MainApplication : Application() {

    private lateinit var ldClient: LDClient
    private lateinit var ldConfig: LDConfig
    private lateinit var ldUser: LDUser
    private lateinit var ldStatusListener: LDStatusListener
    private lateinit var featureFlagChangeListener: FeatureFlagChangeListener
    private lateinit var ldAllFlagsListener : LDAllFlagsListener

    override fun onCreate() {
        super.onCreate()

        featureFlagChangeListener = FeatureFlagChangeListener {
            println("LaunchDarkly registerFeatureFlagListener(): isBiometricEnabled Flag=[$it]")
        }
        ldStatusListener = object : LDStatusListener {
            override fun onConnectionModeChanged(connectionInformation: ConnectionInformation?) {
                println("LaunchDarkly onConnectionModeChanged(): " +
                    "connectionMode=[${connectionInformation?.connectionMode}] " +
                    "lastFailure=[${connectionInformation?.lastFailure}] " +
                    "lastSuccessfulConnection=[${connectionInformation?.lastSuccessfulConnection}] " +
                    "lastFailedConnection=[${connectionInformation?.lastFailedConnection}] ")
            }

            override fun onInternalFailure(ldFailure: LDFailure?) {
                println("LaunchDarkly onInternalFailure(): ldFailure=[$ldFailure]")
            }
        }
        ldConfig = LDConfig.Builder()
            //Coach Roebuck's account...
            // Test
            .mobileKey("mob-c448398a-104d-4e9a-a829-f6e0ef245989")
            // Prod
            // .mobileKey("mob-81151918-ec0c-4163-b56a-7957c6286528")
            .evaluationReasons(true)
            .inlineUsersInEvents(true)
            .disableBackgroundUpdating(false)
            .build()

        val firstName = "Brent"
        val lastName = "Abel"
        val avatar =
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaBJsvhB9uNRXbwnd5kBReTmaGKi9dciJzog&usqp=CAU"
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

        val connectionInformation = ldClient.connectionInformation
        println("LaunchDarkly connectionMode=[${connectionInformation.connectionMode}] ")
        println("LaunchDarkly lastFailedConnection=[${connectionInformation.lastFailedConnection}] ")
        println("LaunchDarkly lastFailure: failureType=[${connectionInformation.lastFailure.failureType}] " +
            "cause=[${connectionInformation.lastFailure.cause}]" +
            "message=[${connectionInformation.lastFailure.message}]")

        ldAllFlagsListener = LDAllFlagsListener {
            println("LaunchDarkly registerAllFlagsListener(): allFlags=[$it]")
        }
        val isInitialized = ldClient.isInitialized
        val isDisableBackgroundPolling = ldClient.isDisableBackgroundPolling
        val isOffline = ldClient.isOffline

        ldClient.registerFeatureFlagListener("isBiometricEnabled", featureFlagChangeListener)
        ldClient.registerAllFlagsListener(ldAllFlagsListener)
        ldClient.registerStatusListener(ldStatusListener)

        println("LaunchDarkly isInitialized=[$isInitialized]")
        println("LaunchDarkly isDisableBackgroundPolling=[$isDisableBackgroundPolling]")
        println("LaunchDarkly isOffline=[$isOffline]")

        ldClient.track("Tracked Event")
        ldClient.trackData("New Data", LDValue.buildArray().add("$firstName $lastName").build())
        ldClient.trackMetric("New Metric",
            LDValue.buildArray().add("$firstName $lastName").build(),
            1.0)
        val one = ldClient.boolVariationDetail("isAmerican", Locale.getDefault() == Locale.US)
        val two = ldClient.boolVariationDetail("isBiometricEnabled", false)
        val three = ldClient.boolVariation("isBiometricEnabled", false)

        println("LaunchDarkly isAmerican detail=[${one.value}] isDefaultValue=[${one.isDefaultValue}] reason=[${one.reason}]")
        println("LaunchDarkly isBiometricEnabled detail=[${two.value}] isDefaultValue=[${two.isDefaultValue}] reason=[${two.reason}]")
        println("LaunchDarkly isBiometricEnabled variation=[${three}]")
    }

    val isBiometricEnabled: Boolean
        get() {
            return ldClient.boolVariation("isBiometricEnabled", false)
        }

    override fun onTerminate() {
        ldClient.unregisterFeatureFlagListener("isBiometricEnabled", featureFlagChangeListener)
        ldClient.unregisterAllFlagsListener(ldAllFlagsListener)
        ldClient.unregisterStatusListener(ldStatusListener)
        ldClient.flush()
        ldClient.close()
        super.onTerminate()
    }
}
