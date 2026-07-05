package com.sruthi.purrrescue.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sruthi.purrrescue.R
import com.sruthi.purrrescue.ui.main.MainActivity

class CatRescueFCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val reportId = message.data["reportId"] ?: return
        val title = message.notification?.title ?: "A stray cat needs help nearby!"
        val body = message.notification?.body ?: "Tap to see if you can rescue it."

        showNotification(reportId, title, body)
    }

    private fun showNotification(reportId: String, title: String, body: String) {
        val channelId = "cat_reports_channel"

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("reportId", reportId)
            putExtra("navigateTo", "catDetail")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            reportId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.crying_cat)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Cat Reports", NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }
        manager.notify(reportId.hashCode(), notification)
    }


    override fun onNewToken(token: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid ?: return)
            .update("fcmToken", token)
    }

}