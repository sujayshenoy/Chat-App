package com.example.chatapp.data.network.retrofit

import com.example.chatapp.common.FIREBASE_MESSAGING_API_KEY
import com.example.chatapp.data.network.retrofit.PushNotificationApi.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class PushNotificationSenderService {
    private val api =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create<PushNotificationApi>()

    suspend fun sendPushNotification(
        message: PushMessage
    ): PushResponse {
        return api.sendPushNotification(FIREBASE_MESSAGING_API_KEY, message)
    }
}