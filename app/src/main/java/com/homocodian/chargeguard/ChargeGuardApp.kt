package com.homocodian.chargeguard

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.homocodian.chargeguard.constant.AppNotification
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChargeGuardApp : Application() {
  override fun onCreate() {
    super.onCreate()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      val batteryLevelMonitorChannel = NotificationChannel(
        AppNotification.BATTERY_LEVEL_MONITOR_CHANNEL_ID,
        AppNotification.BATTERY_LEVEL_MONITOR_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW,
      )

      val chargingLimitReachedChannel = NotificationChannel(
        AppNotification.CHARGING_LIMIT_REACHED_CHANNEL_ID,
        AppNotification.CHARGING_LIMIT_REACHED_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = AppNotification.CHARGING_LIMIT_REACHED_CHANNEL_DESCRIPTION
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        setSound(null, null)
      }

      val notificationManger = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

      notificationManger.createNotificationChannels(
        listOf(
          batteryLevelMonitorChannel,
          chargingLimitReachedChannel
        )
      )
    }
  }
}