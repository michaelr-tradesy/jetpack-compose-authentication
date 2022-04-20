package com.example.jetpackcomposecodelab101.shared

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

//initialization vector (IV)
@Serializable
@Parcelize
data class EncryptedMessage(val cipherText: ByteArray?, val iv: ByteArray?) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedMessage

        if (cipherText != null) {
            if (other.cipherText == null) return false
            if (!cipherText.contentEquals(other.cipherText)) return false
        } else if (other.cipherText != null) return false
        if (iv != null) {
            if (other.iv == null) return false
            if (!iv.contentEquals(other.iv)) return false
        } else if (other.iv != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cipherText?.contentHashCode() ?: 0
        result = 31 * result + (iv?.contentHashCode() ?: 0)
        return result
    }
}
