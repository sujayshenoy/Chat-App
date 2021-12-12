package com.example.chatapp.main

import android.app.Application
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil.Companion.MESSAGE_TOKEN
import com.example.chatapp.common.sharedpreferences.SharedPrefUtilImpl
import com.example.chatapp.firebase.FirebaseMessaging

class ChatApplication : Application() {
    private val logger: Logger = LoggerImpl(this::class.java.simpleName)
    private lateinit var sharedPrefUtil: SharedPrefUtil

    override fun onCreate() {
        super.onCreate()
        sharedPrefUtil = SharedPrefUtilImpl(this@ChatApplication)

        FirebaseMessaging().getToken {
            sharedPrefUtil.addString(MESSAGE_TOKEN, it)
        }
    }
}