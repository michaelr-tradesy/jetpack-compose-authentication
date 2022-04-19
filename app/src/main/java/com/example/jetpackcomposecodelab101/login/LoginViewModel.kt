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
    var isBioMetricsEnabled: MutableState<Boolean>
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
override var isPasswordVisible: MutableState<Boolean>,
override var passwordShowError: MutableState<Boolean>,
override var isPasswordEnabled: MutableState<Boolean>,
override var passwordFocusRequester: FocusRequester,
override var userNameFocusRequester: FocusRequester,
override var userNameShowError: MutableState<Boolean>,
override var isUserNameEnabled: MutableState<Boolean>,
override var isLoginEnabled: MutableState<Boolean>,
override var isBioMetricsEnabled: MutableState<Boolean>,
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
}