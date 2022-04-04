package com.example.jetpackcomposecodelab101.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import com.example.jetpackcomposecodelab101.ui.AppThemeState
import com.example.jetpackcomposecodelab101.ui.DefaultAppThemeState
import com.example.jetpackcomposecodelab101.ui.theme.ColorPalette
import com.example.jetpackcomposecodelab101.ui.theme.JetpackComposeCodeLab101Theme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

interface AppActivity

abstract class DefaultAppActivity :  ComponentActivity(), AppActivity {

    private lateinit var systemUiController: SystemUiController
    private lateinit var appThemeState: MutableState<AppThemeState>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ComposeView(this).also {
            setContentView(it)
        }.setContent {
            PostSetContent(savedInstanceState)
        }
    }

    @Composable
    private fun PostSetContent(savedInstanceState: Bundle?) {
        val isDarkMode = true //isSystemInDarkTheme()
        val systemUiController = rememberSystemUiController()
        appThemeState =
            remember {
                mutableStateOf(
                    DefaultAppThemeState(
                        isDarkTheme = isDarkMode,
                        colorPalette = ColorPalette.Orenji
                    )
                )
            }
        JetpackComposeCodeLab101Theme(
            systemUiController = systemUiController,
            appThemeState = appThemeState.value,
        ) {
            MyApp(savedInstanceState)
        }
    }

    @Composable
    abstract fun MyApp(savedInstanceState: Bundle?)
}