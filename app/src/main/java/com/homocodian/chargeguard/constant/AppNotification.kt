package com.homocodian.chargeguard.constant

object AppNotification {
  const val BATTERY_LEVEL_MONITOR_CHANNEL_ID = "battery_level_monitor_channel_id"
  const val BATTERY_LEVEL_MONITOR_CHANNEL_NAME = "Battery level monitor"

  const val BATTERY_POWER_CONNECTION_MONITOR_CHANNEL_ID = BATTERY_LEVEL_MONITOR_CHANNEL_ID
  const val BATTERY_POWER_CONNECTION_MONITOR_CHANNEL_NAME = BATTERY_LEVEL_MONITOR_CHANNEL_NAME

  const val CHARGING_LIMIT_REACHED_CHANNEL_ID = "charging_limit_reached_id"
  const val CHARGING_LIMIT_REACHED_CHANNEL_NAME = "Charging limit reached"
  const val CHARGING_LIMIT_REACHED_CHANNEL_DESCRIPTION =
    "This channel sends alerts when your device has reached the set charging limit to prevent overcharging. " +
      "An audio notification will be played to ensure you are notified immediately."

  object NotificationId {
    const val BATTERY_LEVEL_MONITOR_NOTIFICATION_ID = 1;
    const val POWER_CONNECTION_STATUS_MONITOR_NOTIFICATION_ID = 2;
    const val CHARGING_LIMIT_REACHED_NOTIFICATION_ID = 3;
  }

  object Foreground {
    const val MONITOR_CHARGING_STATUS_SERVICE_ID = 1
    const val MONITOR_CHARGING_LEVEL_SERVICE_ID = 2
  }
}
