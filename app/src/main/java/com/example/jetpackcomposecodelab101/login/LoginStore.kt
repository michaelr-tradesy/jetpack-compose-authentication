package com.example.jetpackcomposecodelab101.login

import android.content.Context
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import javax.crypto.Cipher

// Following the suggested implementation as specified by arkivanov
// https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/store/Store.kt
// https://arkivanov.github.io/MVIKotlin/
interface LoginStore :
    Store<LoginStore.Intent, LoginStore.State, LoginStore.Label> {

    // Actions that can be triggered in the background in response to receiving intents
    sealed class Action : JvmSerializable {
        object Idle : Action()
    }

    // The user invokes intents
    sealed class Intent : JvmSerializable {
        object Idle : Intent()
        object BioMetricsChanged : Intent() {
            var isEnabled: Boolean = false

            fun values(isEnabled: Boolean): Intent {
                this.isEnabled = isEnabled
                return this
            }
        }
        data class EncryptPassword(
            var context: Context,
            var password: String,
            var cipher: Cipher?
        ) : Intent()

        data class LaunchDashboard(var context: Context) : Intent()
        object UserNameProvided : Intent() {
            var text = ""

            fun values(text: String): Intent {
                this.text = text
                return this
            }
        }

        object PasswordProvided : Intent() {
            var text = ""

            fun values(text: String): Intent {
                this.text = text
                return this
            }
        }

        object FinishedProvidingUserName : Intent()

        object LoginRequested : Intent() {
            var userName = ""
            var password = ""

            fun values(
                userName: String,
                password: String,
            ): Intent {
                this.userName = userName
                this.password = password
                return this
            }
        }

        object ForgotPassword : Intent()
        object SignUpRequested : Intent()
    }

    // Intents produces results
    sealed class Result : JvmSerializable {
        object Idle : Result()
        object PromptForBioMetric : Result()
        data class LaunchDashboard(var context: Context) : Result()
        object UserNameInvalid : Result() {
            var text = ""

            fun values(text: String): Result {
                this.text = text
                return this
            }
        }

        object FocusOnUserName : Result()
        object UserNameInProgress : Result()
        object CanProvidePassword : Result()
        object PasswordInvalid : Result() {
            var text = ""

            fun values(text: String): Result {
                this.text = text
                return this
            }
        }

        object FocusOnPassword : Result()
        object PasswordInProgress : Result()
        object CanLogin : Result()
        object LoginAttemptInProgress : Result()
        object AccessToken : Result()
        object ApiError : Result() {
            var throwable: Throwable? = null

            fun values(throwable: Throwable): Result {
                this.throwable = throwable
                return this
            }
        }

        object Error : Result() {
            var throwable: Throwable? = null

            fun values(throwable: Throwable): Result {
                this.throwable = throwable
                return this
            }
        }
    }

    // Results are transformed into States, which are sent to the user
    sealed class State : JvmSerializable {
        object Idle : State()
        object UserNameInvalid : State() {
            var text: String = ""

            fun values(text: String): State {
                this.text = text
                return this
            }
        }

        data class LoginUiState(
            val isLoginEnabled: Boolean,
            val isPasswordEnabled: Boolean,
            val isUserNameEnabled: Boolean = true,
            val throwable: Throwable? = null,
            val userNameShowError: Boolean = false,
            val passwordShowError: Boolean = false,
            val userName: String? = null,
            val password: String? = null,
            val shouldHideKeyboard: Boolean = false,
            val canShowDashboard: Boolean = false,
            val shouldShowProgress: Boolean = false,
        ) : State()

        object LaunchDashboard : State()
        object FocusOnUserName : State()
        object UserNameInProgress : State()
        object PromptForBioMetric : State()
        object CanProvidePassword : State()
        object PasswordInvalid : State() {
            var text: String = ""

            fun values(text: String): State {
                this.text = text
                return this
            }
        }

        object FocusOnPassword : State()
        object PasswordInProgress : State()
        object CanLogin : State()
        object LoginAttemptInProgress : State()
        object AccessToken : State()
        object ApiError : State() {
            var throwable: Throwable? = null

            fun values(throwable: Throwable?): State {
                this.throwable = throwable
                return this
            }
        }

        object Error : State() {
            var throwable: Throwable? = null

            fun values(throwable: Throwable?): State {
                this.throwable = throwable
                return this
            }
        }

        companion object {
            fun convert(input: Result): State {
//                when (state) {
//                    LoginStore.State.FocusOnPassword -> {
//                        viewModel.focusManager.clearFocus(force = true)
//                        viewModel.passwordFocusRequester.requestFocus()
//                    }
//                    LoginStore.State.FocusOnUserName -> {
//                        viewModel.focusManager.clearFocus(force = true)
//                        viewModel.userNameFocusRequester.requestFocus()
//                    }
//                    LoginStore.State.LoginAttemptInProgress -> {
//                        viewModel.keyboardController?.hide()
//                        viewModel.isUserNameEnabled.value = false
//                        viewModel.isPasswordEnabled.value = false
//                        viewModel.isLoginEnabled.value = false
//                        store.accept(
//                            LoginStore.Intent.LoginAttempt.values(
//                                viewModel.userName.value,
//                                viewModel.password.value
//                            )
//                        )
//                    }
//                }
                return when (input) {
                    is Result.AccessToken -> LoginUiState(
                        isLoginEnabled = true,
                        isPasswordEnabled = true,
                        isUserNameEnabled = true,
                        canShowDashboard = true
                    )
                    is Result.ApiError -> LoginUiState(
                        isLoginEnabled = true,
                        isPasswordEnabled = true,
                        isUserNameEnabled = true,
                        throwable = input.throwable,
                        userNameShowError = false,
                        passwordShowError = false,
                        shouldHideKeyboard = false,
                    )
                    is Result.CanLogin -> LoginUiState(
                        isLoginEnabled = true,
                        isPasswordEnabled = true,
                        isUserNameEnabled = true
                    )
                    is Result.CanProvidePassword -> LoginUiState(
                        isLoginEnabled = false,
                        isPasswordEnabled = true,
                        userNameShowError = false,
                        passwordShowError = false,
                        shouldHideKeyboard = false
                    )
                    is Result.Error -> LoginUiState(
                        isLoginEnabled = true,
                        isPasswordEnabled = true,
                        isUserNameEnabled = true,
                        throwable = input.throwable
                    )
                    is Result.FocusOnUserName -> FocusOnUserName
                    is Result.FocusOnPassword -> FocusOnPassword
                    is Result.Idle -> Idle
                    is Result.LaunchDashboard -> LaunchDashboard
                    is Result.LoginAttemptInProgress -> LoginUiState(
                        isLoginEnabled = false,
                        isPasswordEnabled = false,
                        isUserNameEnabled = false,
                        userNameShowError = false,
                        passwordShowError = false,
                        shouldHideKeyboard = true,
                        shouldShowProgress = true
                    )
                    is Result.PasswordInProgress -> PasswordInProgress
                    is Result.PasswordInvalid -> LoginUiState(
                        isLoginEnabled = false,
                        isPasswordEnabled = true,
                        isUserNameEnabled = true,
                        userNameShowError = false,
                        passwordShowError = true,
                        password = input.text
                    )
                    is Result.UserNameInProgress -> UserNameInProgress
                    is Result.UserNameInvalid -> LoginUiState(
                        isLoginEnabled = false,
                        isPasswordEnabled = false,
                        isUserNameEnabled = true,
                        userNameShowError = true,
                        passwordShowError = false,
                        userName = input.text,
                    )
                    Result.PromptForBioMetric -> PromptForBioMetric
                }
            }
        }
    }

    // Intents can also produce labels, which are also sent to the user
    sealed class Label : JvmSerializable {
        object Idle : Label()
        object InProgress : Label()
        object CanViewDashboard : Label()
        object ProfileRequired : Label()
    }
}
