package com.example.jetpackcomposecodelab101.login

import kotlin.random.Random

interface LoginInteractor {
    fun login(userName: String, password: String): LoginStore.Result
}

class DefaultLoginInteractor : LoginInteractor {
    override fun login(userName: String, password: String): LoginStore.Result {
        return if (Random.nextBoolean()) {
            LoginStore.Result.AccessToken
        } else {
            LoginStore.Result.Error.values(Throwable("Simulation Error"))
        }
    }
}