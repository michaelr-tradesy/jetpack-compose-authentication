package com.example.jetpackcomposecodelab101.dashboard

import androidx.compose.ui.ExperimentalComposeUiApi
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnStartStop
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
import com.arkivanov.mvikotlin.keepers.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.keepers.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.mvikotlin.keepers.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import com.example.jetpackcomposecodelab101.base.launchLogin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

interface DashboardController {
    val stateFlow: MutableStateFlow<DashboardStore.State>
    val labelFlow: MutableStateFlow<DashboardStore.Label>
    fun emit(intent: DashboardStore.Intent)
}

@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalStateKeeperApi
@ExperimentalInstanceKeeperApi
class DefaultDashboardController(
    private val viewModel: DashboardViewModel,
    lifecycle: Lifecycle,
    instanceKeeper: InstanceKeeper,
    private val stateKeeper: StateKeeper<DashboardStore.State>?,
) : DashboardController, BaseMviView<DashboardView.Model, DashboardView.Event>() {

    private val store: DashboardStore
    private val storeFactoryInstance: StoreFactory
    private var binder: Binder? = null

    override val stateFlow: MutableStateFlow<DashboardStore.State> =
        MutableStateFlow(DashboardStore.State.Idle)
    override val labelFlow: MutableStateFlow<DashboardStore.Label> =
        MutableStateFlow(DashboardStore.Label.Idle)

    init {
        lifecycle.doOnStartStop(::onStart, ::onStop)

        storeFactoryInstance =
            LoggingStoreFactory(delegate = TimeTravelStoreFactory(fallback = DefaultStoreFactory))
        val calculatorStore = {
            DashboardStoreFactory(viewModel, storeFactoryInstance).create(stateKeeper)
        }
        store = instanceKeeper.getStore(calculatorStore)

        binder =
            com.arkivanov.mvikotlin.extensions.reaktive.bind {
                store.states.bindTo {
                    onStateReceived(it)
                }
                store.labels.bindTo {
                    println("LoginController: New Label=($it)...")
                    labelFlow.value = it
                }
            }
    }

    private fun onStateReceived(state: DashboardStore.State) {
        viewModel.currentState.value = state
        when (state) {
            is DashboardStore.State.Logout -> {
                state.context.launchLogin()
            }
            DashboardStore.State.AccessToken -> {
                viewModel.isUserNameEnabled.value = true
                viewModel.isPasswordEnabled.value = true
                viewModel.isLoginEnabled.value = true
            }
            DashboardStore.State.ApiError -> {
                viewModel.isUserNameEnabled.value = true
                viewModel.isPasswordEnabled.value = true
                viewModel.isLoginEnabled.value = true
            }
            DashboardStore.State.CanLogin -> {
                viewModel.isLoginEnabled.value = true
                viewModel.passwordShowError.value = false
                viewModel.userNameShowError.value = false
            }
            DashboardStore.State.CanProvidePassword -> {
                viewModel.isPasswordEnabled.value = true
                viewModel.userNameShowError.value = false
                viewModel.passwordShowError.value = false
                viewModel.isLoginEnabled.value = false
            }
            DashboardStore.State.Error -> {
                viewModel.isUserNameEnabled.value = true
                viewModel.isPasswordEnabled.value = true
                viewModel.isLoginEnabled.value = true
            }
            DashboardStore.State.FocusOnPassword -> {
                viewModel.focusManager.clearFocus(force = true)
                viewModel.passwordFocusRequester.requestFocus()
            }
            DashboardStore.State.FocusOnUserName -> {
                viewModel.focusManager.clearFocus(force = true)
                viewModel.userNameFocusRequester.requestFocus()
            }
            DashboardStore.State.Idle -> {}
            DashboardStore.State.LoginAttemptInProgress -> {
                viewModel.isUserNameEnabled.value = false
                viewModel.isPasswordEnabled.value = false
                viewModel.isLoginEnabled.value = false
                store.accept(
                    DashboardStore.Intent.LoginAttempt.values(
                        viewModel.userName.value,
                        viewModel.password.value
                    )
                )
            }
            DashboardStore.State.PasswordInProgress -> {}
            DashboardStore.State.PasswordInvalid -> {
                viewModel.passwordShowError.value = true
                viewModel.isLoginEnabled.value = false
            }
            DashboardStore.State.UserNameInProgress -> {}
            DashboardStore.State.UserNameInvalid -> {
                viewModel.userNameShowError.value = true
                viewModel.passwordShowError.value = false
                viewModel.isLoginEnabled.value = false
            }
        }
        stateFlow.value = state
    }

    private fun onStart() {
        binder?.start()
    }

    private fun onStop() {
        binder?.stop()
    }

    override fun emit(intent: DashboardStore.Intent) {
        store.accept(intent)
    }
}