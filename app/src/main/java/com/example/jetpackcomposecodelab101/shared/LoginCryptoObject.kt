package com.example.jetpackcomposecodelab101.shared

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricPrompt
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface LoginCryptoObject {
    val encryptionObject: BiometricPrompt.CryptoObject
    fun getOrCreateSecretKey(keyName: String): SecretKey
    fun encryptAndSave(context: Context, text: String, cipher: Cipher?): EncryptedMessage
    val decryptionObject: BiometricPrompt.CryptoObject
    fun decrypt(encryptedMessage: EncryptedMessage?, cipher: Cipher)
}

class DefaultLoginCryptoObject : LoginCryptoObject {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val YOUR_SECRET_KEY_NAME = "Y0UR$3CR3TK3YN@M3"
        private const val KEY_SIZE = 128
        private const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    }

    override val encryptionObject get() = BiometricPrompt.CryptoObject(getInitializedCipherForEncryption())
    override val decryptionObject get() = BiometricPrompt.CryptoObject(getInitializedCipherForDecryption())

    private fun getInitializedCipherForEncryption(): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(YOUR_SECRET_KEY_NAME)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        return cipher
    }

    private fun getInitializedCipherForDecryption(
        initializationVector: ByteArray? = null
    ): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(YOUR_SECRET_KEY_NAME)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            GCMParameterSpec(KEY_SIZE, initializationVector)
        )

        return cipher
    }

    private fun getCipher(): Cipher {
        val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
        return Cipher.getInstance(transformation)
    }

    override fun getOrCreateSecretKey(keyName: String): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null) // Keystore must be loaded before it can be accessed
        keyStore.getKey(keyName, null)?.let { return it as SecretKey }

        val paramsBuilder = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        paramsBuilder.apply {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(true)
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParams)

        return keyGenerator.generateKey()
    }

    override fun encryptAndSave(context: Context, text: String, cipher: Cipher?): EncryptedMessage {
        return encryptData(text, cipher ?: getCipher())

        // TODO: SharedPreferences, Room, etc.
//        storeEncryptedMessage(
//            context,
//            prefKey = encryptedMessage.savedAt.toString(),
//            encryptedMessage = encryptedMessage
//        )
        // END TODO
    }

    private fun encryptData(plaintext: String, cipher: Cipher): EncryptedMessage {
        val ciphertext = cipher
            .doFinal(plaintext.toByteArray(Charset.forName("UTF-8")))
        return EncryptedMessage(ciphertext, cipher.iv)
    }

    override fun decrypt(encryptedMessage: EncryptedMessage?, cipher: Cipher) {
        encryptedMessage?.cipherText?.let { it ->
            val decryptedMessage = decryptData(it, cipher)
            println("decryptedMessage=[$decryptedMessage]")
        }
    }

    private fun decryptData(ciphertext: ByteArray, cipher: Cipher): String {
        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, Charset.forName("UTF-8"))
    }
}