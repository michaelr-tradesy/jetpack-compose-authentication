package com.example.jetpackcomposecodelab101.dashboard

import com.arkivanov.mvikotlin.core.view.MviView

interface DashboardView  : MviView<DashboardView.Model, DashboardView.Event> {

    data class Model(
        val value: String
    )

    sealed class Event {
        object IncrementClicked: Event()
        object DecrementClicked: Event()
    }
}
