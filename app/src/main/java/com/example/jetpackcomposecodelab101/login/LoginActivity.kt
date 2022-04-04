package com.example.jetpackcomposecodelab101.login

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import com.arkivanov.mvikotlin.core.lifecycle.asMviLifecycle
import com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
import com.arkivanov.mvikotlin.keepers.instancekeeper.getInstanceKeeper
import com.arkivanov.mvikotlin.keepers.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.mvikotlin.keepers.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.keepers.statekeeper.get
import com.arkivanov.mvikotlin.keepers.statekeeper.getSerializableStateKeeperRegistry
import com.example.jetpackcomposecodelab101.R
import com.example.jetpackcomposecodelab101.base.AppViewModelFactory
import com.example.jetpackcomposecodelab101.base.DefaultAppActivity
import com.example.jetpackcomposecodelab101.ui.DefaultAppThemeState
import com.example.jetpackcomposecodelab101.ui.theme.ColorPalette
import com.example.jetpackcomposecodelab101.ui.theme.JetpackComposeCodeLab101Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@DelicateCoroutinesApi
@ExperimentalInstanceKeeperApi
@ExperimentalStateKeeperApi
class LoginActivity : DefaultAppActivity() {

    // TODO: There's a type of flow that can survive Process Dev.
    
    private lateinit var viewModel: LoginViewModel
    private lateinit var controller: LoginController
    private lateinit var coroutineScope: CoroutineScope

    @ExperimentalStateKeeperApi
    companion object {
        private var stateKeeper: StateKeeper<LoginStore.State>? = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stateKeeper?.unregister()
    }

    @Composable
    override fun MyApp(savedInstanceState: Bundle?) {
        val viewModel: LoginViewModel by viewModels { AppViewModelFactory(this) }

//        val vm = remember { mutableStateOf(DefaultLoginViewModel()) }
//        viewModel = vm.value
        coroutineScope = rememberCoroutineScope()
        viewModel.focusManager = LocalFocusManager.current
        viewModel.keyboardController = LocalSoftwareKeyboardController.current
        viewModel.currentState = remember { mutableStateOf(LoginStore.State.Idle) }
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

        controller = DefaultLoginController(
            viewModel,
            lifecycle.asMviLifecycle(),
            instanceKeeper,
            stateKeeper,
            this
        )

        LaunchedEffect(Unit) {
            controller.stateFlow.collect(object : FlowCollector<LoginStore.State> {
                override suspend fun emit(value: LoginStore.State) {
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

    private fun onStateReceived(value: LoginStore.State) {
        println("LoginActivity: New State Received=[$value]")
        when (value) {
            LoginStore.State.ApiError -> {}
            LoginStore.State.CanLogin -> {}
            LoginStore.State.CanProvidePassword -> {}
            LoginStore.State.Error -> {}
            LoginStore.State.FocusOnPassword -> {}
            LoginStore.State.FocusOnUserName -> {}
            LoginStore.State.Idle -> {}
            LoginStore.State.LoginAttemptInProgress -> {}
            LoginStore.State.PasswordInProgress -> {}
            LoginStore.State.PasswordInvalid -> {}
            LoginStore.State.UserNameInProgress -> {}
            LoginStore.State.UserNameInvalid -> {}
            LoginStore.State.AccessToken -> {
                controller.emit(LoginStore.Intent.LaunchDashboard(this))
            }
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
        viewModel = DefaultLoginViewModel()
        viewModel.currentState = remember { mutableStateOf(LoginStore.State.Idle) }
        JetpackComposeCodeLab101Theme(
            systemUiController = systemUiController,
            appThemeState = appThemeState.value,
        ) {
            DefaultView()
        }
    }

    @Composable
    private fun DefaultView() {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.login_text),
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                )
                CreateUserNameField()
                Divider(color = Color.LightGray)
                CreatePasswordField()
                CreateLoginButtonField()
                CreateForgotPasswordField()
                CreateSignUpField()
            }
        }
    }

    @Composable
    private fun CreateUserNameField() {
        viewModel.isUserNameEnabled = rememberSaveable { mutableStateOf(true) }
        viewModel.userNameShowError = rememberSaveable { mutableStateOf(false) }
        viewModel.userName = rememberSaveable { mutableStateOf("") }
        viewModel.userNameFocusRequester = remember { FocusRequester() }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(viewModel.userNameFocusRequester),
            enabled = viewModel.isUserNameEnabled.value,
            value = viewModel.userName.value,
            onValueChange = {
                controller.emit(LoginStore.Intent.UserNameProvided.values(it))
            },
            placeholder = {
                Text(text = stringResource(R.string.enter_user_name_text))
            },
            label = { Text(stringResource(R.string.user_name_text)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    controller.emit(LoginStore.Intent.FinishedProvidingUserName)
                }
            ),
            singleLine = true,
            isError = viewModel.userNameShowError.value,
            visualTransformation = VisualTransformation.None,
            colors = TextFieldDefaults.outlinedTextFieldColors()
        )
//        DisposableEffect(Unit) {
//            viewModel.userNameFocusRequester.requestFocus()
//            viewModel.keyboardController?.show()
//            onDispose { }
//        }
    }

    @Composable
    private fun CreatePasswordField() {
        viewModel.password = rememberSaveable { mutableStateOf("") }
        viewModel.isPasswordVisible = remember { mutableStateOf(false) }
        viewModel.isPasswordEnabled = remember { mutableStateOf(false) }
        viewModel.passwordShowError = remember { mutableStateOf(false) }
        viewModel.password = rememberSaveable { mutableStateOf("") }
        viewModel.passwordFocusRequester = remember { FocusRequester() }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(viewModel.passwordFocusRequester),
            enabled = viewModel.isPasswordEnabled.value,
            value = viewModel.password.value,
            onValueChange = {
                controller.emit(LoginStore.Intent.PasswordProvided.values(it))
            },
            placeholder = {
                Text(text = stringResource(R.string.enter_password_text))
            },
            label = { Text(stringResource(R.string.password_text)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    controller.emit(LoginStore.Intent.FinishedProvidingPassword)
                }
            ),
            singleLine = true,
            isError = viewModel.passwordShowError.value,
            visualTransformation =
            if (viewModel.isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val (icon, iconColor) = if (viewModel.isPasswordVisible.value) {
                    Pair(Icons.Filled.Visibility, MaterialTheme.colors.secondary)
                } else {
                    Pair(Icons.Filled.VisibilityOff, MaterialTheme.colors.secondaryVariant)
                }

                IconButton(onClick = {
                    viewModel.isPasswordVisible.value = !viewModel.isPasswordVisible.value
                }) {
                    Icon(
                        icon,
                        contentDescription = if (viewModel.isPasswordVisible.value) {
                            stringResource(R.string.password_visible_text)
                        } else {
                            stringResource(R.string.password_invisible_text)
                        },
                        tint = iconColor
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors()
        )
    }

    @Composable
    private fun CreateLoginButtonField() {
        viewModel.responseText = rememberSaveable { mutableStateOf("") }
        viewModel.isLoginEnabled = rememberSaveable { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                viewModel.userNameShowError.value -> {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colors.error,
                        text = stringResource(id = R.string.user_name_invalid_text)
                    )
                }
                viewModel.passwordShowError.value -> {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colors.error,
                        text = stringResource(id = R.string.password_invalid_text)
                    )
                }
                viewModel.currentState.value is LoginStore.State.ApiError -> {
                    val message =
                        (viewModel.currentState.value as LoginStore.State.ApiError).throwable?.message
                            ?: stringResource(
                                id = R.string.internal_server_error
                            )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colors.error,
                        text = message
                    )
                }
                viewModel.currentState.value is LoginStore.State.Error -> {
                    val message =
                        (viewModel.currentState.value as LoginStore.State.Error).throwable?.message
                            ?: stringResource(
                                id = R.string.internal_server_error
                            )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colors.error,
                        text = message
                    )
                }
                viewModel.currentState.value == LoginStore.State.LoginAttemptInProgress -> {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.logging_in_text)
                    )
                    CreateCircularProgressIndicator(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        progress = 0f
                    )
                }
            }
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                enabled = viewModel.isLoginEnabled.value,
                onClick = { controller.emit(LoginStore.Intent.LoginRequested) }
            ) {
                Text(stringResource(R.string.login_text))
            }
        }
    }

    @Composable
    private fun CreateCircularProgressIndicator(
        modifier: Modifier = Modifier,
        progress: Float = 0.0f,
        color: Color = MaterialTheme.colors.primary,
        strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val progressAnimationValue by infiniteTransition.animateFloat(
            initialValue = progress,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(animation = tween(900))
        )

        CircularProgressIndicator(
            modifier = modifier,
            progress = progressAnimationValue,
            color = color,
            strokeWidth = strokeWidth
        )
    }

    @Composable
    private fun CreateForgotPasswordField() {
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            enabled = viewModel.currentState.value != LoginStore.State.LoginAttemptInProgress,
            onClick = { }
        ) {
            Text(stringResource(R.string.forgot_password_text))
        }
    }

    @Composable
    private fun CreateSignUpField() {
        Text(stringResource(R.string.dont_have_an_account))
        Text(
            modifier = Modifier.clickable {
                if (viewModel.currentState.value != LoginStore.State.LoginAttemptInProgress) {
                    println("Sign Up Clicked")
                }
            },
            style = TextStyle(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary
            ),
            text = stringResource(R.string.create_account_text)
        )
    }
}

