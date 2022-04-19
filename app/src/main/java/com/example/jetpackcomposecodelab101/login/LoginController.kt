package com.example.jetpackcomposecodelab101.login

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
import com.example.jetpackcomposecodelab101.base.launchDashboard
import kotlinx.coroutines.flow.MutableStateFlow

interface LoginController {
    val stateFlow: MutableStateFlow<LoginStore.State>
    val labelFlow: MutableStateFlow<LoginStore.Label>
    fun emit(intent: LoginStore.Intent)
}

@ExperimentalComposeUiApi
@ExperimentalStateKeeperApi
@ExperimentalInstanceKeeperApi
class DefaultLoginController(
    private val viewModel: LoginViewModel,
    lifecycle: Lifecycle,
    instanceKeeper: InstanceKeeper,
    private val stateKeeper: StateKeeper<LoginStore.State>?
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
        val calculatorStore = {
            LoginStoreFactory(viewModel, storeFactoryInstance).create(stateKeeper)
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