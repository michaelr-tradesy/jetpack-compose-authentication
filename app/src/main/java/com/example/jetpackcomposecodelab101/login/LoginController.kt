package com.example.jetpackcomposecodelab101.login

import android.content.Context
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
import com.example.jetpackcomposecodelab101.DefaultPreferencesAdapter
import com.example.jetpackcomposecodelab101.base.launchDashboard
import com.example.jetpackcomposecodelab101.google.DefaultGoogleCredentialsAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

interface LoginController {
    val stateFlow: MutableStateFlow<LoginStore.State>
    val labelFlow: MutableStateFlow<LoginStore.Label>
    fun emit(intent: LoginStore.Intent)
}

@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalStateKeeperApi
@ExperimentalInstanceKeeperApi
class DefaultLoginController(
    private val viewModel: LoginViewModel,
    lifecycle: Lifecycle,
    instanceKeeper: InstanceKeeper,
    private val stateKeeper: StateKeeper<LoginStore.State>?,
    context: Context
) : LoginController, BaseMviView<LoginView.Model, LoginView.Event>() {

    private val store: LoginStore
    private val storeFactoryInstance: StoreFactory
    private var binder: Binder? = null

    override val stateFlow: MutableStateFlow<LoginStore.State> =
        MutableStateFlow(LoginStore.State.Idle)
    override val labelFlow: MutableStateFlow<LoginStore.Label> =
        MutableStateFlow(LoginStore.Label.Idle)

    init {
        lifecycle.doOnStartStop(::onStart, ::onStop)

        storeFactoryInstance =
            LoggingStoreFactory(delegate = TimeTravelStoreFactory(fallback = DefaultStoreFactory))
        val interactor: LoginInteractor = DefaultLoginInteractor(DefaultGoogleCredentialsAdapter(
            DefaultPreferencesAdapter(context)
        ))
        val calculatorStore = {
            LoginStoreFactory(context, viewModel, storeFactoryInstance, interactor).create(stateKeeper)
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

    private fun onStateReceived(state: LoginStore.State) {
        viewModel.currentState.value = state
        when (state) {
            is LoginStore.State.LaunchDashboard -> {
                state.context.launchDashboard()
            }
            LoginStore.State.AccessToken -> {
                viewModel.isUserNameEnabled.value = true
                viewModel.isPasswordEnabled.value = true
                viewModel.isLoginEnabled.value = true
            }
            LoginStore.State.ApiError -> {
                viewModel.isUserNameEnabled.value = true
                viewModel.isPasswordEnabled.value = true
                viewModel.isLoginEnabled.value = true
            }
            LoginStore.State.CanLogin -> {
                viewModel.isLoginEnabled.value = true
                viewModel.passwordShowError.value = false
                viewModel.userNameShowError.value = false
            }
            LoginStore.State.CanProvidePassword -> {
                viewModel.isPasswordEnabled.value = true
                viewModel.userNameShowError.value = false
                viewModel.passwordShowError.value = false
                viewModel.isLoginEnabled.value = false
            }
            LoginStore.State.Error -> {
                viewModel.isUserNameEnabled.value = true
                viewModel.isPasswordEnabled.value = true
                viewModel.isLoginEnabled.value = true
            }
            LoginStore.State.FocusOnPassword -> {
                viewModel.focusManager.clearFocus(force = true)
                viewModel.passwordFocusRequester.requestFocus()
            }
            LoginStore.State.FocusOnUserName -> {
                viewModel.focusManager.clearFocus(force = true)
                viewModel.userNameFocusRequester.requestFocus()
            }
            LoginStore.State.Idle -> {}
            LoginStore.State.LoginAttemptInProgress -> {
                viewModel.keyboardController?.hide()
                viewModel.isUserNameEnabled.value = false
                viewModel.isPasswordEnabled.value = false
                viewModel.isLoginEnabled.value = false
                store.accept(
                    LoginStore.Intent.LoginAttempt.values(
                        viewModel.userName.value,
                        viewModel.password.value
                    )
                )
            }
            LoginStore.State.PasswordInProgress -> {}
            LoginStore.State.PasswordInvalid -> {
                viewModel.passwordShowError.value = true
                viewModel.isLoginEnabled.value = false
            }
            LoginStore.State.UserNameInProgress -> {}
            LoginStore.State.UserNameInvalid -> {
                viewModel.userNameShowError.value = true
                viewModel.passwordShowError.value = false
                viewModel.isLoginEnabled.value = false
            }
            LoginStore.State.GoogleCredentialsDeleted -> TODO()
            LoginStore.State.GoogleCredentialsError -> TODO()
            LoginStore.State.GoogleCredentialsReceived -> TODO()
            LoginStore.State.GoogleCredentialsSaved -> TODO()
            LoginStore.State.GoogleDownloadLocation -> TODO()
            LoginStore.State.GooglePermissionDenied -> TODO()
            LoginStore.State.GooglePermissionsRequested -> TODO()
        }
        stateFlow.value = state
    }

    private fun onStart() {
        binder?.start()
    }

    private fun onStop() {
        binder?.stop()
    }

    override fun emit(intent: LoginStore.Intent) {
        store.accept(intent)
    }
}