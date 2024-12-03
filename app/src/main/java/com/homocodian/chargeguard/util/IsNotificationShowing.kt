package com.homocodian.chargeguard.util

import android.app.NotificationManager
import android.content.Context

fun isNotificationShowing(context: Context, notificationId: Int): Boolean {
  val notificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// Check if the notification with this ID already exists
  val existingNotification = notificationManager.activeNotifications.find {
    it.id == notificationId && it.packageName == context.packageName
  }

  return existingNotification != null
}