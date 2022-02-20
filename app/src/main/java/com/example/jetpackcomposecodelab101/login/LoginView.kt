package com.example.jetpackcomposecodelab101.login

import com.arkivanov.mvikotlin.core.view.MviView

interface LoginView  : MviView<LoginView.Model, LoginView.Event> {

    data class Model(
        val value: String
    )

    sealed class Event {
        object IncrementClicked: Event()
        object DecrementClicked: Event()
    }
}
