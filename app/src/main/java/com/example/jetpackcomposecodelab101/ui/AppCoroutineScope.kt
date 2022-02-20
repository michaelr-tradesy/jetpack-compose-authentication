package com.example.jetpackcomposecodelab101.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

data class AppCoroutineScope(override val coroutineContext: CoroutineContext = Dispatchers.IO) :
    CoroutineScope