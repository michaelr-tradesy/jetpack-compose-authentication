package com.example.jetpackcomposecodelab101.login

import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.ExperimentalComposeUiApi
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.keepers.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.mvikotlin.keepers.statekeeper.StateKeeper
import com.example.jetpackcomposecodelab101.ui.AppCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalStateKeeperApi
class LoginStoreFactory(
    private val viewModel: LoginViewModel,
    private val storeFactory: StoreFactory,
    private val interactor: LoginInteractor = DefaultLoginInteractor(),
    private val coroutineScope: CoroutineScope = AppCoroutineScope(),
) {
    private object ReducerImpl :
        Reducer<LoginStore.State, LoginStore.Result> {
        override fun LoginStore.State.reduce(result: LoginStore.Result): LoginStore.State =
            LoginStore.State.convert(result)
    }

    private class ExecutorImpl(
        private val viewModel: LoginViewModel,
        private val interactor: LoginInteractor,
        private val coroutineScope: CoroutineScope,
        private val mainScope: CoroutineScope = MainScope(),
    ) :
        ReaktiveExecutor<LoginStore.Intent, LoginStore.Action, LoginStore.State, LoginStore.Result, LoginStore.Label>() {

        // region Override Methods

        override fun executeAction(action: LoginStore.Action, getState: () -> LoginStore.State) {
            when (action) {
                is LoginStore.Action.Idle -> TODO("Not yet implemented=[$action]")
            }
        }

        override fun executeIntent(intent: LoginStore.Intent, getState: () -> LoginStore.State) {
            when (intent) {
                is LoginStore.Intent.Idle -> TODO("Not yet implemented=[$intent]")
                is LoginStore.Intent.UserNameProvided -> onUserNameProvided(intent.text)
                is LoginStore.Intent.PasswordProvided -> onPasswordProvided(intent.text)
                is LoginStore.Intent.LoginRequested -> onLoginRequested(
                    getState(),
                    intent.userName,
                    intent.password
                )
                is LoginStore.Intent.ForgotPassword -> TODO("Not yet implemented=[$intent]")
                is LoginStore.Intent.SignUpRequested -> TODO("Not yet implemented=[$intent]")
                is LoginStore.Intent.FinishedProvidingUserName -> onFinishedProvidingUserName(
                    getState()
                )
                is LoginStore.Intent.LaunchDashboard -> onLaunchDashboard(intent.context)
            }
        }

        private fun onLaunchDashboard(context: Context) {
            dispatch(LoginStore.Result.LaunchDashboard(context))
        }

        // endregion

        // region Private Methods

        // region Process UserName

        private fun onFinishedProvidingUserName(state: LoginStore.State) {
            if (state is LoginStore.State.LoginUiState && state.isPasswordEnabled) {
                dispatch(LoginStore.Result.FocusOnPassword)
            } else {
                dispatch(LoginStore.Result.FocusOnUserName)
            }
        }

        private fun onUserNameProvided(input: String) {
            setUserName(input)
//            publish(LoginStore.Label.Idle)
            val isUserNameValid = isUserNameValid(input)
            if (isUserNameValid) {
                dispatch(LoginStore.Result.CanProvidePassword)
            } else {
                dispatch(LoginStore.Result.UserNameInvalid.values(input))
            }
        }

        private fun setUserName(text: String) {
            viewModel.userName.value = text
        }

        private fun isUserNameValid(text: String) = text.length >= 5

        // endregion

        // region Process Password

        private fun onPasswordProvided(input: String) {
            setPassword(input)
//            publish(LoginStore.Label.Idle)
            val isPasswordValid = isPasswordValid(input)
            if (isPasswordValid) {
                dispatch(LoginStore.Result.CanLogin)
            } else {
                dispatch(LoginStore.Result.PasswordInvalid.values(""))
            }
        }

        private fun setPassword(text: String) {
            viewModel.password.value = text
        }

        private fun isPasswordValid(text: String) = text.length >= 5

        // endregion

        // region Login Attempt

        private fun onLoginRequested(state: LoginStore.State, userName: String, password: String) {
            if (state is LoginStore.State.LoginUiState && state.isLoginEnabled) {
                coroutineScope.launch {
                    delay(3000)
                    mainScope.launch {
                        dispatch(interactor.login(userName, password))
                    }
                }
                dispatch(LoginStore.Result.LoginAttemptInProgress)
            }
        }

        // endregion

        @Deprecated(
            "I don't think this will be required in Jetpack Compose",
            ReplaceWith("None")
        )
        private fun wasGoPressed(actionId: Int, keyEvent: KeyEvent?) =
            (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE
                    || (keyEvent?.action == KeyEvent.ACTION_UP
                    && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER))

        @Deprecated(
            "I don't think this will be required in Jetpack Compose",
            ReplaceWith("None")
        )
        private fun isUserDoneEditing(actionId: Int, keyEvent: KeyEvent?) =
            (actionId == EditorInfo.IME_ACTION_NEXT
                    || (keyEvent?.action == KeyEvent.ACTION_UP
                    && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER))

        // endregion
    }

    fun create(stateKeeper: StateKeeper<LoginStore.State>?): LoginStore =
        object : LoginStore,
            Store<LoginStore.Intent, LoginStore.State, LoginStore.Label> by storeFactory.create(
                name = this@LoginStoreFactory::class.java.simpleName,
                initialState = LoginStore.State.Idle,
                executorFactory = { ExecutorImpl(viewModel, interactor, coroutineScope) },
                reducer = ReducerImpl
            ) {
        }.also {
            stateKeeper?.register {
                // We can reset any transient state here
                it.state
            }
        }
}
