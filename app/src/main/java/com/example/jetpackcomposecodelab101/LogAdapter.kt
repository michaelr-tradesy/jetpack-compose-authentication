package com.example.jetpackcomposecodelab101

import android.util.Log

interface LogAdapter {
    fun v(message: String)
    fun e(message: String)
    fun w(message: String)
    fun i(message: String)
    fun d(message: String)

    companion object {
        const val minimumLevels = 5
        const val targetIndex = 4

        const val Verbose = "Client-Verbose"
        const val Info = "Client-Info"
        const val Debug = "Client-Debug"
        const val Warning = "Client-Warning"
        const val Error = "Client-Error"
    }
}

class DefaultLogAdapter: LogAdapter {

    override fun v(message: String) {
        log(LogAdapter.Verbose, message)
    }

    override fun e(message: String) {
        log(LogAdapter.Error, message)
    }

    override fun w(message: String) {
        log(LogAdapter.Warning, message)
    }

    override fun i(message: String) {
        log(LogAdapter.Info, message)
    }

    override fun d(message: String) {
        log(LogAdapter.Debug, message)
    }

    private fun log(tagName: String,
                    message: String) {

        if (BuildConfig.DEBUG
            || tagName.equals(LogAdapter.Error, ignoreCase = true)
            || tagName.equals(LogAdapter.Warning, ignoreCase = true)) {
            val stacktrace = Thread.currentThread().stackTrace
            val e: StackTraceElement
            e = if (stacktrace.size >= LogAdapter.minimumLevels) {
                stacktrace[LogAdapter.targetIndex]
            } else {
                stacktrace[stacktrace.size - 1]
            }
            val methodName = e.methodName
            var className = e.className
            val index = className.lastIndexOf(".")
            className = className.substring(index + 1)

            when {
                tagName.equals(LogAdapter.Debug, ignoreCase = true) -> Log.d(LogAdapter.Debug, "$className::$methodName(): $message")
                tagName.equals(LogAdapter.Warning, ignoreCase = true) -> Log.w(LogAdapter.Warning, "$className::$methodName(): $message")
                tagName.equals(LogAdapter.Info, ignoreCase = true) -> Log.i(LogAdapter.Info, "$className::$methodName(): $message")
                tagName.equals(LogAdapter.Error, ignoreCase = true) -> Log.e(LogAdapter.Error, "$className::$methodName(): $message")
                tagName.equals(LogAdapter.Verbose, ignoreCase = true) -> Log.v(LogAdapter.Verbose, "$className::$methodName(): $message")
            }
        }
    }
}