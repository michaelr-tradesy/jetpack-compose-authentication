package com.example.jetpackcomposecodelab101.base

import android.os.Bundle
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
import com.arkivanov.mvikotlin.keepers.statekeeper.ExperimentalStateKeeperApi
import com.example.jetpackcomposecodelab101.dashboard.DashboardActivity
import com.example.jetpackcomposecodelab101.dashboard.DashboardViewModel
import com.example.jetpackcomposecodelab101.dashboard.DefaultDashboardViewModel
import com.example.jetpackcomposecodelab101.login.DefaultLoginViewModel
import com.example.jetpackcomposecodelab101.login.LoginActivity
import com.example.jetpackcomposecodelab101.login.LoginViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalStateKeeperApi
@ExperimentalInstanceKeeperApi
@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@ExperimentalComposeUiApi
class AppViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val output = when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DefaultDashboardViewModel(handle)
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                DefaultLoginViewModel(handle)
            }
            else -> throw Throwable("Class Not Recognized=[$modelClass]")
        }

        return output as T
    }
}