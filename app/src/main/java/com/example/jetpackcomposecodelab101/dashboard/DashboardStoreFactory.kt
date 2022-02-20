package com.example.jetpackcomposecodelab101.dashboard

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
import kotlinx.coroutines.*

@ExperimentalComposeUiApi
@ExperimentalStateKeeperApi
class DashboardStoreFactory(
    private val viewModel: DashboardViewModel,
    private val storeFactory: StoreFactory,
    private val interactor: DashboardInteractor = DefaultDashboardInteractor(),
    private val coroutineScope: CoroutineScope = AppCoroutineScope()
) {
    private object ReducerImpl : Reducer<DashboardStore.State, DashboardStore.Result> {
        override fun DashboardStore.State.reduce(result: DashboardStore.Result): DashboardStore.State =
            DashboardStore.State.convert(result)
    }

    private class ExecutorImpl(
        private val viewModel: DashboardViewModel,
        private val interactor: DashboardInteractor,
        private val coroutineScope: CoroutineScope,
        private val mainScope: CoroutineScope = MainScope(),
    ) :
        ReaktiveExecutor<DashboardStore.Intent, DashboardStore.Action, DashboardStore.State, DashboardStore.Result, DashboardStore.Label>() {

        // region Override Methods

        override fun executeAction(action: DashboardStore.Action, getState: () -> DashboardStore.State) {
            when (action) {
                is DashboardStore.Action.Idle -> TODO("Not yet implemented=[$action]")
            }
        }

        override fun executeIntent(intent: DashboardStore.Intent, getState: () -> DashboardStore.State) {
            when (intent) {
                is DashboardStore.Intent.Idle -> TODO("Not yet implemented=[$intent]")
                is DashboardStore.Intent.UserNameProvided -> onUserNameProvided(intent.text)
                is DashboardStore.Intent.PasswordProvided -> onPasswordProvided(intent.text)
                is DashboardStore.Intent.LoginRequested -> onLoginRequested()
                is DashboardStore.Intent.ForgotPassword -> TODO("Not yet implemented=[$intent]")
                is DashboardStore.Intent.SignUpRequested -> TODO("Not yet implemented=[$intent]")
                is DashboardStore.Intent.FinishedProvidingPassword -> onLoginRequested()
                is DashboardStore.Intent.FinishedProvidingUserName -> onFinishedProvidingUserName(
                    getState()
                )
                is DashboardStore.Intent.Logout -> onLogout(intent.context)
                is DashboardStore.Intent.LoginAttempt -> onLoginAttempt(
                    intent.userName,
                    intent.password
                )
            }
        }

        private fun onLogout(context: Context) {
            dispatch(DashboardStore.Result.Logout(context))
        }

        // endregion

        // region Private Methods

        // region Process UserName

        private fun onFinishedProvidingUserName(state: DashboardStore.State) {
            if (state is DashboardStore.State.CanProvidePassword) {
                dispatch(DashboardStore.Result.FocusOnPassword)
            } else {
                dispatch(DashboardStore.Result.FocusOnUserName)
            }
        }

        private fun onUserNameProvided(input: String) {
            setUserName(input)
//            publish(LoginStore.Label.Idle)
            val isUserNameValid = isUserNameValid(input)
            if (isUserNameValid) {
                dispatch(DashboardStore.Result.CanProvidePassword)
            } else {
                dispatch(DashboardStore.Result.UserNameInvalid.values(input))
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
                dispatch(DashboardStore.Result.CanLogin)
            } else {
                dispatch(DashboardStore.Result.PasswordInvalid.values(""))
            }
        }

        private fun setPassword(text: String) {
            viewModel.password.value = text
        }

        private fun isPasswordValid(text: String) = text.length >= 5

        // endregion

        // region Login Attempt

        private fun onLoginAttempt(userName: String, password: String) {
            coroutineScope.launch {
                delay(3000)
                mainScope.launch {
                    dispatch(interactor.login(userName, password))
                }
            }
        }

        private fun onLoginRequested() {
            dispatch(DashboardStore.Result.LoginAttemptInProgress)
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

    fun create(stateKeeper: StateKeeper<DashboardStore.State>?): DashboardStore =
        object : DashboardStore,
            Store<DashboardStore.Intent, DashboardStore.State, DashboardStore.Label> by storeFactory.create(
                name = this@DashboardStoreFactory::class.java.simpleName,
                initialState = DashboardStore.State.Idle,
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
