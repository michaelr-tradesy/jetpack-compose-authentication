package com.example.jetpackcomposecodelab101

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

interface PreferencesAdapter {
    var longitude: Float?
    var latitude: Float?
}

class DefaultPreferencesAdapter(
    context: Context,
    private var sharedPreference: SharedPreferences? = context.getSharedPreferences(
        context.resources.getString(R.string.shared_preferences_name
        ),
        Activity.MODE_PRIVATE)
) : PreferencesAdapter {

    var domainProtocol: String?
        get() = getSharedPreferenceString(domainProtocolKey)
        set(value) {
            setSharedPreferenceString(domainProtocolKey, value)
        }
    var domain: String?
        get() = getSharedPreferenceString(preferredDomainKey)
        set(value) {
            setSharedPreferenceString(preferredDomainKey, value)
        }
    var accessToken: String?
        get() = getSharedPreferenceString(accessTokenKey)
        set(value) {
            setSharedPreferenceString(accessTokenKey, value)
        }
    var profile: String?
        get() = getSharedPreferenceString(profileKey)
        set(value) {
            setSharedPreferenceString(profileKey, value)
        }
    var fontName: String?
        get() = getSharedPreferenceString(fontNameKey)
        set(value) {
            setSharedPreferenceString(fontNameKey, value)
        }
    var email: String?
        get() = getSharedPreferenceString(emailKey)
        set(value) {
            setSharedPreferenceString(emailKey, value)
        }
    var key: String?
        get() = getSharedPreferenceString(passwordKey)
        set(value) {
            setSharedPreferenceString(passwordKey, value)
        }
    var firstTimeLaunch: Boolean?
        get() = getSharedPreferenceBoolean(firstTimeLaunchKey)
        set(value) {
            setSharedPreferenceBoolean(firstTimeLaunchKey, value)
        }
    var useLegacy: Boolean?
        get() = getSharedPreferenceBoolean(useLegacyKey)
        set(value) {
            setSharedPreferenceBoolean(useLegacyKey, value)
        }
    var isRememberDeviceEnabled: Boolean?
        get() = getSharedPreferenceBoolean(isRememberDeviceEnabledKey)
        set(value) {
            setSharedPreferenceBoolean(isRememberDeviceEnabledKey, value)
        }
    var didCompleteStartup: Boolean?
        get() = getSharedPreferenceBoolean(didCompleteStartupKey)
        set(value) {
            setSharedPreferenceBoolean(didCompleteStartupKey, value)
        }
    var isDiscoverable: Boolean?
        get() = getSharedPreferenceBoolean(isDiscoverableKey)
        set(value) {
            setSharedPreferenceBoolean(isDiscoverableKey, value)
        }
    var environment: Int?
        get() = getSharedPreferenceInteger(environmentKey)
        set(value) {
            setSharedPreferenceInteger(environmentKey, value)
        }
    var minimumAge: Int?
        get() = getSharedPreferenceInteger(minimumAgeKey)
        set(value) {
            setSharedPreferenceInteger(minimumAgeKey, value)
        }
    var maximumAge: Int?
        get() = getSharedPreferenceInteger(maximumAgeKey)
        set(value) {
            setSharedPreferenceInteger(maximumAgeKey, value)
        }
    var proximity: Int?
        get() = getSharedPreferenceInteger(proximityKey)
        set(value) {
            setSharedPreferenceInteger(proximityKey, value)
        }
    var apiLoggingLevel: Int?
        get() = getSharedPreferenceInteger(apiLoggingLevelKey)
        set(value) {
            setSharedPreferenceInteger(apiLoggingLevelKey, value)
        }
    override var latitude: Float?
        get() = getSharedPreferenceFloat(latitudeKey)
        set(value) {
            setSharedPreferenceFloat(latitudeKey, value)
        }
    override var longitude: Float?
        get() = getSharedPreferenceFloat(longitudeKey)
        set(value) {
            setSharedPreferenceFloat(longitudeKey, value)
        }

    // region Private Methods

    private fun setSharedPreferenceBoolean(key: String, value: Boolean?): Boolean? {

        var result: Boolean? = true
        val settings = sharedPreference

        if (settings != null) {
            if (value == null) {
                settings.edit().remove(key).apply()
            } else {
                settings.edit().putBoolean(key, value).apply()
            }
        } else {
            result = false
        }

        return result
    }

    private fun setSharedPreferenceInteger(key: String, value: Int?): Boolean? {

        var result: Boolean? = true
        val settings = sharedPreference

        if (settings != null) {
            if (value == null) {
                settings.edit().remove(key).apply()
            } else {
                settings.edit().putInt(key, value).apply()
            }
        } else {
            result = false
        }

        return result
    }

    private fun setSharedPreferenceLong(key: String, value: Long?): Boolean? {

        var result: Boolean? = true
        val settings = sharedPreference

        if (settings != null) {
            if (value == null) {
                settings.edit().remove(key).apply()
            } else {
                settings.edit().putLong(key, value).apply()
            }
        } else {
            result = false
        }

        return result
    }

    private fun setSharedPreferenceFloat(key: String, value: Float?): Boolean? {

        var result: Boolean? = true
        val settings = sharedPreference

        if (settings != null) {
            if (value == null) {
                settings.edit().remove(key).apply()
            } else {
                settings.edit().putFloat(key, value).apply()
            }
        } else {
            result = false
        }

        return result
    }

    private fun setSharedPreferenceString(key: String, value: String?): Boolean? {

        var result: Boolean? = true
        val settings = sharedPreference

        if (settings != null) {
            if (value == null) {
                settings.edit().remove(key).apply()
            } else {
                settings.edit().putString(key, value).apply()
            }
        } else {
            result = false
        }

        return result
    }

    private fun getSharedPreferenceBoolean(key: String): Boolean? {

        var value: Boolean? = false
        val settings = sharedPreference

        if (settings != null) {
            value = settings.getBoolean(key, false)
        }

        return value
    }

    private fun getSharedPreferenceInteger(key: String): Int? {

        var value: Int? = Integer.MIN_VALUE
        val settings = sharedPreference

        if (settings != null) {
            value = settings.getInt(key, Integer.MIN_VALUE)
        }

        return value
    }

    private fun getSharedPreferenceLong(key: String): Long? {

        var value: Long? = Long.MIN_VALUE
        val settings = sharedPreference

        if (settings != null) {
            value = settings.getLong(key, Long.MIN_VALUE)
        }

        return value
    }

    private fun getSharedPreferenceFloat(key: String): Float? {

        var value: Float? = Float.MIN_VALUE
        val settings = sharedPreference

        if (settings != null) {
            value = settings.getFloat(key, Float.MIN_VALUE)
        }

        return value
    }

    private fun getSharedPreferenceString(key: String): String? {

        var value: String? = null
        val settings = sharedPreference

        if (settings != null) {
            value = settings.getString(key, "")
        }

        return value
    }

    // endregion

    companion object {
        const val preferredDomainKey = "PreferredDomain"
        const val accessTokenKey = "AccessToken"
        const val firstTimeLaunchKey: String = "firstTimeLaunch"
        const val isRememberDeviceEnabledKey = "isRememberDeviceEnabled"
        const val didCompleteStartupKey = "didCompleteStartupKey"
        const val profileKey = "profileKey"
        const val fontNameKey = "fontNameKey"
        const val isDiscoverableKey = "isDiscoverable"
        const val minimumAgeKey = "minimumAge"
        const val maximumAgeKey = "maximumAge"
        const val proximityKey = "proximity"
        const val emailKey = "email"
        const val passwordKey = "key"
        const val latitudeKey = "latitude"
        const val longitudeKey = "longitude"
        const val environmentKey = "environment"
        const val domainProtocolKey = "domain protocol"
        const val apiLoggingLevelKey = "API Logging Level"
        const val useLegacyKey = "Use Legacy Media Database Table"
    }
}