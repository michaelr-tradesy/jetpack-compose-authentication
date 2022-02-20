package com.example.jetpackcomposecodelab101.main

interface MainViewModel {
}

class DefaultMainViewModel(private val navigator: MainNavigator = DefaultMainNavigator()) : MainViewModel