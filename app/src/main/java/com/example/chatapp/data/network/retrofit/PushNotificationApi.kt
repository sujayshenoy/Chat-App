package com.example.chatapp.data.network.retrofit

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface PushNotificationApi {
    @Headers("Accept: application/json")
    @POST(SEND_PUSH_ENDPOINT)
    suspend fun sendPushNotification(
        @Header("Authorization") authToken: String,
        @Body message: PushMessage
    ): PushResponse

    companion object {
        const val BASE_URL = "https://fcm.googleapis.com"
        const val SEND_PUSH_ENDPOINT = "fcm/send"
    }
}