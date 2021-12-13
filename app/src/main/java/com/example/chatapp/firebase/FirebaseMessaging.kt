package com.example.chatapp.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
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

    companion object {
        const val CHANNEL_ID = "123"
    }

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
        val imgUrl = message.notification?.imageUrl.toString() ?: ""

        createNotificationChannel()
        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = if (imgUrl.isEmpty()) {
            createTextNotification(title, pendingIntent, content)
        } else {
            createImageNotification(title, pendingIntent, imgUrl)
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random.nextInt(), notification)
    }

    private fun createTextNotification(
        title: String,
        pendingIntent: PendingIntent,
        text: String
    ): Notification {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setContentText(text)
            .build()
    }

    private fun createImageNotification(
        title: String,
        pendingIntent: PendingIntent,
        imageUrl: String
    ): Notification {
        val imgBitmap = Glide.with(this).asBitmap().load(imageUrl).submit().get()
        val notificationImage = NotificationCompat.BigPictureStyle().bigPicture(imgBitmap)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setStyle(notificationImage)
            .build()
    }

    private fun createNotificationChannel() {
        val name = "Fundo Notify Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}