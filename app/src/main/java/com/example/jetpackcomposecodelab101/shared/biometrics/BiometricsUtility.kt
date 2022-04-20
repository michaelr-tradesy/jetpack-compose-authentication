package com.example.jetpackcomposecodelab101.shared.biometrics

import android.content.Context
import androidx.biometric.BiometricManager

interface BiometricsUtility {
    fun isBiometricReady(context: Context): Boolean
}

class DefaultBiometricsUtility : BiometricsUtility {
    override fun isBiometricReady(context: Context) =
        hasBiometricCapability(context) == BiometricManager.BIOMETRIC_SUCCESS

    private fun hasBiometricCapability(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    }
}