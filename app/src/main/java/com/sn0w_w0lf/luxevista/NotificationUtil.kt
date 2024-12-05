package com.sn0w_w0lf.luxevista

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationUtil {

    private const val CHANNEL_ID = "luxevista_notifications"
    private const val CHANNEL_NAME = "LuxeVista Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for new rooms and promotions"
    private const val NOTIFICATION_ID = 1  // Use a constant or dynamic ID for multiple notifications

    @SuppressLint("ObsoleteSdkInt")
    fun createNotification(context: Context, title: String, message: String) {
        // Create Notification Channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Intent that will be fired when the notification is clicked
        val intent = Intent(context, NotificationActivity::class.java).apply {
            // You can add extra data to the intent if needed
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_MESSAGE", message)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Use the appropriate flags
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Change this to your custom icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Auto-cancel when tapped
            .build()

        // Get the NotificationManager system service
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification) // Use a dynamic ID for multiple notifications
    }
}
