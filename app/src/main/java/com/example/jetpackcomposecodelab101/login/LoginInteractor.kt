package com.example.jetpackcomposecodelab101.login

import android.content.Context
import android.os.Bundle
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.example.jetpackcomposecodelab101.google.GoogleCredentialsAdapter
import kotlin.random.Random

interface LoginInteractor {
    fun onRestoreInstanceState(savedInstanceState: Bundle?)
    fun onSaveInstanceState(outState: Bundle?)
    fun login(userName: String, password: String): LoginStore.Result
    fun requestCredentials(context: Context, callback: (LoginStore.Result) -> Unit)
}

class DefaultLoginInteractor(private val googleCredentialsAdapter: GoogleCredentialsAdapter) : LoginInteractor {
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        googleCredentialsAdapter.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        googleCredentialsAdapter.onSaveInstanceState(outState)
    }

    override fun login(userName: String, password: String): LoginStore.Result {
        return if (Random.nextBoolean()) {
            LoginStore.Result.AccessToken
        } else {
            LoginStore.Result.Error.values(Throwable("Simulation Error"))
        }
    }

    override fun requestCredentials(context: Context, callback: (LoginStore.Result) -> Unit) {
        googleCredentialsAdapter.requestCredentials(context, callback)
    }
}