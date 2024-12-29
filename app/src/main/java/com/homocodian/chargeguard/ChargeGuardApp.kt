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
        AppNotification.CHARGING_LEVEL_MONITOR_CHANNEL_ID,
        AppNotification.CHARGING_LEVEL_MONITOR_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW,
      ).apply {
        description = AppNotification.BATTERY_LEVEL_MONITOR_CHANNEL_DESCRIPTION
      }

      val powerConnectionMonitorChannel = NotificationChannel(
        AppNotification.POWER_CONNECTION_MONITOR_CHANNEL_ID,
        AppNotification.POWER_CONNECTION_MONITOR_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW,
      ).apply {
        description = AppNotification.POWER_CONNECTION_MONITOR_CHANNEL_DESCRIPTION
      }

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
          powerConnectionMonitorChannel,
          batteryLevelMonitorChannel,
          chargingLimitReachedChannel
        )
      )
    }
  }
}