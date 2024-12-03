package com.homocodian.chargeguard.util

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.homocodian.chargeguard.R

object NotificationCreator {
  fun create(context: Context, channelId: String, title: String, text: String): Notification {
    return NotificationCompat.Builder(context, channelId)
      .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title).setContentText(text)
      .build()
  }
}