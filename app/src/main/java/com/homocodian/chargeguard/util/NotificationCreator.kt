package com.homocodian.chargeguard.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.homocodian.chargeguard.MainActivity
import com.homocodian.chargeguard.R
import com.homocodian.chargeguard.constant.IntentCode

object NotificationCreator {
  fun create(
    context: Context,
    channelId: String,
    title: String,
    text: String,
    pendingIntent: PendingIntent? = null
  ): Notification {
    val notification = NotificationCompat.Builder(context, channelId)
      .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title).setContentText(text)

    if (pendingIntent != null) {
      notification.setContentIntent(pendingIntent)
    } else {
      val activityIntent = Intent(context, MainActivity::class.java)
      val pendingIntent = PendingIntent.getActivity(
        context,
        IntentCode.MAIN_ACTIVITY,
        activityIntent,
        PendingIntent.FLAG_IMMUTABLE
      )
      notification.setContentIntent(pendingIntent)
    }

    return notification.build()
  }
}