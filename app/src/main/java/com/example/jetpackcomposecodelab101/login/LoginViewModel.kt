package com.example.jetpackcomposecodelab101.login

import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.jetpackcomposecodelab101.shared.EncryptedMessage
import com.example.jetpackcomposecodelab101.shared.LoginCryptoObject
import javax.crypto.Cipher

@ExperimentalComposeUiApi
interface LoginViewModel {
    fun encryptPassword(context: Context, password: String, cipher: Cipher?): EncryptedMessage

    val encryptionObject: BiometricPrompt.CryptoObject?
    var currentState: MutableState<LoginStore.State>
    var responseText: MutableState<String>
    var focusManager: FocusManager
    var isPasswordVisible: MutableState<Boolean>
    var passwordShowError: MutableState<Boolean>
    var isPasswordEnabled: MutableState<Boolean>
    var passwordFocusRequester: FocusRequester
    var userNameFocusRequester: FocusRequester
    var userNameShowError: MutableState<Boolean>
    var isUserNameEnabled: MutableState<Boolean>
    var userName: MutableState<String>
    var password: MutableState<String>
    var bioMetricsPassword: MutableState<EncryptedMessage?>
    var isLoginEnabled: MutableState<Boolean>
    var keyboardController : SoftwareKeyboardController?
    var isBioMetricsEnabled: MutableState<Boolean>
    val loginCrypto: LoginCryptoObject
    val decryptionObject: BiometricPrompt.CryptoObject
}

@ExperimentalComposeUiApi
class DefaultLoginViewModel(
    // Data is stored as a Bundle
    private val savedStateHandle: SavedStateHandle? = null,
    override var keyboardController : SoftwareKeyboardController? = null,
    override var responseText: MutableState<String>,
    override var focusManager: FocusManager,
    override var currentState: MutableState<LoginStore.State>,
    override var userName: MutableState<String>,
    override var password: MutableState<String>,
    override var bioMetricsPassword: MutableState<EncryptedMessage?>,
    override var isPasswordVisible: MutableState<Boolean>,
    override var passwordShowError: MutableState<Boolean>,
    override var isPasswordEnabled: MutableState<Boolean>,
    override var passwordFocusRequester: FocusRequester,
    override var userNameFocusRequester: FocusRequester,
    override var userNameShowError: MutableState<Boolean>,
    override var isUserNameEnabled: MutableState<Boolean>,
    override var isLoginEnabled: MutableState<Boolean>,
    override var isBioMetricsEnabled: MutableState<Boolean>,
    override val loginCrypto: LoginCryptoObject
) : ViewModel(), LoginViewModel {

    companion object {
        const val UserNameKey = "userName"
        const val PasswordKey = "password"
    }

    private fun wasGoPressed(actionId: Int, keyEvent: KeyEvent?) =
        (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE
                || (keyEvent?.action == KeyEvent.ACTION_UP
                && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER))

    private fun isUserDoneEditing(actionId: Int, keyEvent: KeyEvent?) =
        (actionId == EditorInfo.IME_ACTION_NEXT
                || (keyEvent?.action == KeyEvent.ACTION_UP
                && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER))

    override fun encryptPassword(context: Context, password: String, cipher: Cipher?): EncryptedMessage {
        return loginCrypto.encryptAndSave(context, password, cipher)
    }

    override val encryptionObject: BiometricPrompt.CryptoObject get() = loginCrypto.encryptionObject
    override val decryptionObject: BiometricPrompt.CryptoObject get() = loginCrypto.decryptionObject
}