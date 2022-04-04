package com.example.jetpackcomposecodelab101.login

import android.content.Context
import android.view.KeyEvent
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable

// Following the suggested implementation as specified by arkivanov
// https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/store/Store.kt
// https://arkivanov.github.io/MVIKotlin/
interface LoginStore :
    Store<LoginStore.Intent, LoginStore.State, LoginStore.Label> {

    // Actions that can be triggered in the background in response to receiving intents
    sealed class Action : JvmSerializable {
        object Idle : Action()
        data class GoogleCredentialsRequested(var context: Context) : Action()
    }

    // The user invokes intents
    sealed class Intent : JvmSerializable {
        object Idle : Intent()
        data class GoogleCredentialsRequested(val context: Context) : Intent()
        data class LaunchDashboard(var context: Context): Intent()
        object  UserNameProvided : Intent() {
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
        object FinishedProvidingUserName : Intent() {
            var actionId: Int = 0
            var keyEvent: KeyEvent? = null

            fun values(actionId: Int, keyEvent: KeyEvent?): Intent {
                this.actionId = actionId
                this.keyEvent = keyEvent
                return this
            }
        }
        object FinishedProvidingPassword : Intent() {
            var actionId: Int = 0
            var keyEvent: KeyEvent? = null

            fun values(actionId: Int, keyEvent: KeyEvent?): Intent {
                this.actionId = actionId
                this.keyEvent = keyEvent
                return this
            }
        }
        object LoginAttempt : Intent() {
            var userName = ""
            var password = ""

            fun values(userName: String, password: String): Intent {
                this.userName = userName
                this.password = password
                return this
            }
        }
        object LoginRequested : Intent()
        object ForgotPassword : Intent()
        object SignUpRequested : Intent()
    }

    // Intents produces results
    sealed class Result : JvmSerializable {
        object Idle : Result()
        data class LaunchDashboard(var context: Context): Result()
        object UserNameInvalid : Result() {
            var text = ""

            fun values(text: String) : Result {
                this.text = text
                return this
            }
        }
        object GoogleCredentialsError: Result()
        object GoogleCredentialsReceived : Result() {
            var username = ""
            var password: String? = ""

            fun values(username: String, password: String?) : Result {
                this.username = username
                this.password = password
                return this
            }
        }
        object GooglePermissionsRequested : Result()
        object GooglePermissionDenied : Result()
        object GoogleDownloadLocation : Result()
        object GoogleCredentialsSaved : Result()
        object GoogleCredentialsDeleted : Result()
        object FocusOnUserName : Result()
        object UserNameInProgress : Result()
        object CanProvidePassword : Result()
        object PasswordInvalid : Result() {
            var text = ""

            fun values(text: String) : Result {
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

            fun values(throwable: Throwable) : Result {
                this.throwable = throwable
                return this
            }
        }
        object Error : Result() {
            var throwable: Throwable? = null

            fun values(throwable: Throwable) : Result {
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

            fun values(text: String) : State {
                this.text = text
                return this
            }
        }
        data class LaunchDashboard(var context: Context): State()
        object GoogleCredentialsReceived : State() {
            var username = ""
            var password: String? = ""

            fun values(username: String, password: String?) : State {
                this.username = username
                this.password = password
                return this
            }
        }
        object GoogleCredentialsError : State()
        object GooglePermissionsRequested : State()
        object GooglePermissionDenied : State()
        object GoogleDownloadLocation : State()
        object GoogleCredentialsSaved : State()
        object GoogleCredentialsDeleted : State()
        object FocusOnUserName : State()
        object UserNameInProgress : State()
        object CanProvidePassword : State()
        object PasswordInvalid : State() {
            var text: String = ""

            fun values(text: String) : State {
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

            fun values(throwable: Throwable?) : State {
                this.throwable = throwable
                return this
            }
        }
        object Error : State() {
            var throwable: Throwable? = null

            fun values(throwable: Throwable?) : State {
                this.throwable = throwable
                return this
            }
        }

        companion object {
            fun convert(input: Result): State {
                return when (input) {
                    is Result.Idle -> Idle
                    is Result.UserNameInvalid -> UserNameInvalid.values(input.text)
                    is Result.FocusOnUserName -> FocusOnUserName
                    is Result.UserNameInProgress -> UserNameInProgress
                    is Result.CanProvidePassword -> CanProvidePassword
                    is Result.PasswordInvalid -> PasswordInvalid.values(input.text)
                    is Result.FocusOnPassword -> FocusOnPassword
                    is Result.PasswordInProgress -> PasswordInProgress
                    is Result.CanLogin -> CanLogin
                    is Result.LoginAttemptInProgress -> LoginAttemptInProgress
                    is Result.ApiError -> ApiError.values(input.throwable)
                    is Result.Error -> Error.values(input.throwable)
                    is Result.AccessToken -> AccessToken
                    is Result.LaunchDashboard -> LaunchDashboard(input.context)
                    is Result.GoogleCredentialsDeleted -> GoogleCredentialsDeleted
                    is Result.GoogleCredentialsReceived -> GoogleCredentialsReceived.values(
                        input.username,
                        input.password
                    )
                    is Result.GoogleCredentialsSaved -> GoogleCredentialsSaved
                    is Result.GoogleDownloadLocation -> GoogleDownloadLocation
                    is Result.GooglePermissionDenied -> GooglePermissionDenied
                    is Result.GooglePermissionsRequested -> GooglePermissionsRequested
                    Result.GoogleCredentialsError -> GoogleCredentialsError
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