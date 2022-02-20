package com.example.jetpackcomposecodelab101.login

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

@ExperimentalComposeUiApi
interface LoginViewModel {
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
    var isLoginEnabled: MutableState<Boolean>
    var keyboardController : SoftwareKeyboardController?
}

@ExperimentalComposeUiApi
class DefaultLoginViewModel(
    // Data is stored as a Bundle
    private val savedStateHandle: SavedStateHandle? = null
) : ViewModel(), LoginViewModel {

    companion object {
        const val UserNameKey = "userName"
        const val PasswordKey = "password"
    }

    override var keyboardController : SoftwareKeyboardController? = null
    override lateinit var responseText: MutableState<String>
    override lateinit var focusManager: FocusManager
    override lateinit var currentState: MutableState<LoginStore.State>

    override lateinit var userName: MutableState<String>
    override lateinit var password: MutableState<String>

    override lateinit var isPasswordVisible: MutableState<Boolean>
    override lateinit var passwordShowError: MutableState<Boolean>
    override lateinit var isPasswordEnabled: MutableState<Boolean>
    override lateinit var passwordFocusRequester: FocusRequester
    override lateinit var userNameFocusRequester: FocusRequester
    override lateinit var userNameShowError: MutableState<Boolean>
    override lateinit var isUserNameEnabled: MutableState<Boolean>
    override lateinit var isLoginEnabled: MutableState<Boolean>

    private fun wasGoPressed(actionId: Int, keyEvent: KeyEvent?) =
        (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE
                || (keyEvent?.action == KeyEvent.ACTION_UP
                && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER))

    private fun isUserDoneEditing(actionId: Int, keyEvent: KeyEvent?) =
        (actionId == EditorInfo.IME_ACTION_NEXT
                || (keyEvent?.action == KeyEvent.ACTION_UP
                && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER))
}