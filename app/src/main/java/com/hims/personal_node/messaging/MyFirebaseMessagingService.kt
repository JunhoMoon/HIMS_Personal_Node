package com.hims.personal_node

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    // 메시지 수신
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: "+ remoteMessage.from)

        if (remoteMessage.data != null) {
            val data = remoteMessage.data
            val title = data["title"]
            val messagae = data["message"]

            println("messagaeTest : "+ messagae)

            sendNotification(title, messagae)
        }
    }

    private fun sendNotification(title: String?, message: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val CHANNEL_ID = "CollocNotification"
        val CHANNEL_NAME = "CollocChannel"
        val description = "This is Colloc channel"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}