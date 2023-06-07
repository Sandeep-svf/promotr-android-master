package com.freqwency.promotr.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.freqwency.promotr.R
import com.freqwency.promotr.view.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class FirebaseObserver : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.size > 0) {
            Log.e("info_status", "onMessageReceived123")
        }


        // Log.e("info_status", "onMessageReceived123  "+remoteMessage.getNotification().getSound()+" "+strMyId);

        // Check if message contains a notification payload.
        Log.e("BookActivity", "onMessageReceived123  " + remoteMessage.data)
        if (remoteMessage.notification != null) {
            sendNotification(remoteMessage.notification!!.body)
        }
    }

    private fun sendNotification(messageBody: String?) {
        val pendingIntent: PendingIntent
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //  PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                this,
                0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this,
                0 /* Request code */,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val channelId = System.currentTimeMillis().toString() + ""
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationBuilder.setContentTitle(getString(R.string.app_name))
        notificationBuilder.setContentText(messageBody)
        notificationBuilder.setAutoCancel(true)
        //notificationBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+getPackageName()+"/"+R.raw.tring_sms));
        notificationBuilder.setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager != null) {
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(true)
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            val notificationId =
                (Date(System.currentTimeMillis()).time / 1000L % Int.MAX_VALUE).toInt()
            notificationManager.notify(
                notificationId /* ID of notification */,
                notificationBuilder.build()
            )
        }
    }
}