package com.example.jetpackcomposecodelab101.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.ui.ExperimentalComposeUiApi
import com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
import com.arkivanov.mvikotlin.keepers.statekeeper.ExperimentalStateKeeperApi
import com.example.jetpackcomposecodelab101.dashboard.DashboardActivity
import com.example.jetpackcomposecodelab101.login.LoginActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalInstanceKeeperApi
@ExperimentalCoroutinesApi
@OptIn(InternalCoroutinesApi::class, kotlinx.coroutines.DelicateCoroutinesApi::class,
    com.arkivanov.mvikotlin.keepers.statekeeper.ExperimentalStateKeeperApi::class
)
@ExperimentalComposeUiApi
fun Context.launchDashboard() {
    navigate(clazz = DashboardActivity::class.java)
}

@ExperimentalInstanceKeeperApi
@ExperimentalCoroutinesApi
@OptIn(InternalCoroutinesApi::class, kotlinx.coroutines.DelicateCoroutinesApi::class,
    com.arkivanov.mvikotlin.keepers.statekeeper.ExperimentalStateKeeperApi::class
)
@ExperimentalComposeUiApi
fun Context.launchLogin() {
    navigate(clazz = LoginActivity::class.java)
}

private fun Context.navigate(
    clazz: Class<*>,
    flags: Int = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK,
    bundle: Bundle? = null
) {
    val intent = Intent(this, clazz)
    intent.flags = flags
    bundle?.let { intent.putExtras(it) }
    startActivity(intent)
}

