package com.homocodian.chargeguard.util.helper

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.homocodian.chargeguard.MainActivity
import com.homocodian.chargeguard.R
import com.homocodian.chargeguard.constant.IntentCode

object NotificationHelper {
  fun notify(applicationContext: Context, id: Int, notification: Notification) {
    val notificationManager =
      applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(id, notification)
  }

  fun createNotification(
    context: Context,
    channelId: String,
    title: String,
    text: String,
    pendingIntent: PendingIntent? = null
  ): Notification {
    val notification = getBasicBuilder(context, channelId, title, text, pendingIntent)
    return notification.build()
  }

  fun getBasicBuilder(
    context: Context,
    channelId: String,
    title: String,
    text: String,
    pendingIntent: PendingIntent? = null
  ): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, channelId).apply {
      setSmallIcon(R.drawable.ic_launcher_foreground)
      setContentTitle(title).setContentText(text)
      if (pendingIntent != null) {
        setContentIntent(pendingIntent)
      } else {
        val activityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
          context,
          IntentCode.MAIN_ACTIVITY,
          activityIntent,
          PendingIntent.FLAG_IMMUTABLE
        )
        setContentIntent(pendingIntent)
      }
    }
  }
}