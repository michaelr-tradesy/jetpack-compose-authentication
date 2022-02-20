package com.example.jetpackcomposecodelab101.dashboard

import kotlin.random.Random

interface DashboardInteractor {
    fun login(userName: String, password: String): DashboardStore.Result
}

class DefaultDashboardInteractor : DashboardInteractor {
    override fun login(userName: String, password: String): DashboardStore.Result {
        return if (Random.nextBoolean()) {
            DashboardStore.Result.AccessToken
        } else {
            DashboardStore.Result.Error.values(Throwable("Simulation Error"))
        }
    }
}