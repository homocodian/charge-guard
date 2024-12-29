package com.homocodian.chargeguard.constant

object AppNotification {
  const val CHARGING_LEVEL_MONITOR_CHANNEL_ID = "battery_level_monitor_channel_id"
  const val CHARGING_LEVEL_MONITOR_CHANNEL_NAME = "Battery Level Monitor"
  const val BATTERY_LEVEL_MONITOR_CHANNEL_DESCRIPTION = "This notification channel is used to provide updates " +
    "when the charging level of the device is being monitored."

  const val POWER_CONNECTION_MONITOR_CHANNEL_ID = "power_connection_channel_id"
  const val POWER_CONNECTION_MONITOR_CHANNEL_NAME = "Power Connection Monitor"
  const val POWER_CONNECTION_MONITOR_CHANNEL_DESCRIPTION =
    "This notification channel is used to provide alerts " +
      "when the device's power connection status is being monitored."

  const val CHARGING_LIMIT_REACHED_CHANNEL_ID = "charging_limit_reached_id"
  const val CHARGING_LIMIT_REACHED_CHANNEL_NAME = "Charging Limit Reached"
  const val CHARGING_LIMIT_REACHED_CHANNEL_DESCRIPTION =
    "This channel sends alerts with an audio when your device has reached the set charging " +
      "limit to prevent overcharging."

  object NotificationId {
    // const val CHARGING_LEVEL_MONITOR_NOTIFICATION_ID = 1
    // const val POWER_CONNECTION_MONITOR_NOTIFICATION_ID = 2
    const val CHARGING_LIMIT_REACHED_NOTIFICATION_ID = 3
    const val MISSED_LIMIT_REACHED_NOTIFICATION_ID = 4
  }

  object Foreground {
    const val POWER_CONNECTION_SERVICE_ID = 1
    const val CHARGING_LEVEL_SERVICE_ID = 2
  }
}
