package com.example.chatapp.common.logger

import android.util.Log
import kotlin.Error

class LoggerImpl(private val scope: String): Logger {
    override fun logInfo(message: String) {
        Log.i(scope, message)
    }

    override fun logError(message: String) {
        Log.e(scope, message)
    }
}