package com.mertadali.walkietalkie

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseService : FirebaseMessagingService() {

    val channelId = "firebase_channel"

    @SuppressLint("ServiceCast")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, ChatFragment::class.java)
        // Açık kalan activitylerin hepsini kapatır. finish çağırmak gibidir.
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 1, intent, FLAG_ONE_SHOT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = java.util.Random().nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.data["user"])
            .setContentText(message.data["text"])
            .setContentText(message.data["downloadUrl"])
            .setSmallIcon(R.drawable.baseline_message_24)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()



        notificationManager.notify(notificationId, notification)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "firebaseChannel"

        val channel = NotificationChannel(channelId, channelName, IMPORTANCE_HIGH).apply {
            description = "Notification Channel Description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}