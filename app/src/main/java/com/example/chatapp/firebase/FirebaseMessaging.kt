package com.example.chatapp.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.chatapp.R
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.ui.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseMessaging : FirebaseMessagingService() {
    private val fbMessageInstance = FirebaseMessaging.getInstance()
    private val logger: Logger = LoggerImpl("FirebaseMessaging")

    fun getToken(callback: (String) -> Unit) {
        fbMessageInstance.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                logger.logError("Failed getting token")
                callback("")
            }

            callback(task.result.toString())
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: ""
        val content = message.notification?.body ?: ""
        val imgUrl = message.notification?.imageUrl ?: ""

        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(applicationContext, Random.nextInt().toString())
                .setSmallIcon(R.drawable.ic_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(content)
                .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random.nextInt(), notification)
    }
}