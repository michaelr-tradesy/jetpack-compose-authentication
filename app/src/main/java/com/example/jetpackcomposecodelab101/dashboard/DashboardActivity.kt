package com.example.jetpackcomposecodelab101.dashboard

import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.arkivanov.mvikotlin.core.lifecycle.asMviLifecycle
import com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
import com.arkivanov.mvikotlin.keepers.instancekeeper.getInstanceKeeper
import com.arkivanov.mvikotlin.keepers.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.mvikotlin.keepers.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.keepers.statekeeper.get
import com.arkivanov.mvikotlin.keepers.statekeeper.getSerializableStateKeeperRegistry
import com.example.jetpackcomposecodelab101.R
import com.example.jetpackcomposecodelab101.base.DefaultAppActivity
import com.example.jetpackcomposecodelab101.login.LoginActivity
import com.example.jetpackcomposecodelab101.ui.DefaultAppThemeState
import com.example.jetpackcomposecodelab101.ui.theme.ColorPalette
import com.example.jetpackcomposecodelab101.ui.theme.JetpackComposeCodeLab101Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@ExperimentalStateKeeperApi
@ExperimentalInstanceKeeperApi
@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@ExperimentalComposeUiApi
class DashboardActivity : DefaultAppActivity() {
    private lateinit var viewModel: DashboardViewModel
    private lateinit var controller: DashboardController
    private lateinit var coroutineScope: CoroutineScope

    @ExperimentalStateKeeperApi
    companion object {
        private var stateKeeper: StateKeeper<DashboardStore.State>? = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stateKeeper?.unregister()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun MyApp(savedInstanceState: Bundle?) {
//        val viewModel: DashboardViewModel by viewModels { AppViewModelFactory(this) }
//
//        this.viewModel = viewModel
        val vm = remember { mutableStateOf(DefaultDashboardViewModel()) }
        viewModel = vm.value
        coroutineScope = rememberCoroutineScope()
        viewModel.focusManager = LocalFocusManager.current
        viewModel.keyboardController = LocalSoftwareKeyboardController.current
        viewModel.currentState = remember { mutableStateOf(DashboardStore.State.Idle) }
        CreateMviKotlinController()
        DefaultView()
    }

    @Composable
    private fun CreateMviKotlinController() {
        val instanceKeeper = getInstanceKeeper()
        val stateKeeperRegistry = getSerializableStateKeeperRegistry()

        if (stateKeeper == null) {
            stateKeeper = stateKeeperRegistry.get()
        }

        controller = DefaultDashboardController(
            viewModel,
            lifecycle.asMviLifecycle(),
            instanceKeeper,
            stateKeeper
        )

        LaunchedEffect(Unit) {
            @Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
            controller.stateFlow.collect(object : FlowCollector<DashboardStore.State> {
                override suspend fun emit(value: DashboardStore.State) {
                    onStateReceived(value)
                }
            })
        }

        DisposableEffect(lifecycle) {
            // Create an observer that triggers our remembered callbacks
            // for sending analytics events
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    println("event=[$event]")
                } else if (event == Lifecycle.Event.ON_STOP) {
                    println("event=[$event]")
                }
            }

            // Add the observer to the lifecycle
            lifecycle.addObserver(observer)

            // When the effect leaves the Composition, remove the observer
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
        SideEffect {

        }
    }

    private fun onStateReceived(value: DashboardStore.State) {
        println("DashboardActivity: New State Received=[$value]")
        when (value) {
            DashboardStore.State.ApiError -> {}
            DashboardStore.State.CanLogin -> {}
            DashboardStore.State.CanProvidePassword -> {}
            DashboardStore.State.Error -> {}
            DashboardStore.State.FocusOnPassword -> {}
            DashboardStore.State.FocusOnUserName -> {}
            DashboardStore.State.Idle -> {}
            DashboardStore.State.LoginAttemptInProgress -> {}
            DashboardStore.State.PasswordInProgress -> {}
            DashboardStore.State.PasswordInvalid -> {}
            DashboardStore.State.UserNameInProgress -> {}
            DashboardStore.State.UserNameInvalid -> {}
            DashboardStore.State.AccessToken -> {
                controller.emit(DashboardStore.Intent.Logout(this))
            }
            is DashboardStore.State.Logout -> { }
        }
    }

    @Preview(
        fontScale = 1f,
        name = "Light Mode",
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        showSystemUi = true,
        showBackground = true
    )
    @Preview(
        fontScale = 1f,
        name = "Dark Mode",
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        showSystemUi = true,
        showBackground = true
    )
    @Composable
    fun Preview() {
        val systemUiController = rememberSystemUiController()
        val isDarkMode = isSystemInDarkTheme()
        val appThemeState =
            remember {
                mutableStateOf(
                    DefaultAppThemeState(
                        isSystemModeEnable = true,
                        isDarkTheme = isDarkMode,
                        colorPalette = ColorPalette.Orenji
                    )
                )
            }
        JetpackComposeCodeLab101Theme(
            systemUiController = systemUiController,
            appThemeState = appThemeState.value,
        ) {
            DefaultView()
        }
    }

    @Composable
    private fun DefaultView() {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.dashboard_text))
                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    onClick = { onLogout() }
                ) {
                    Text(stringResource(R.string.logout_text))
                }
            }
        }
    }

    private fun onLogout() {
        controller.emit(DashboardStore.Intent.Logout(this))
    }
}
