package com.homocodian.chargeguard.constant

object BatteryLevel {
    const val DEFAULT_BATTERY_LEVEL_TO_MONITOR = 80

    object DataStore {
        const val BATTERY_LEVEL_TO_MONITOR_KEY = "BATTERY_LEVEL_TO_MONITOR"
        const val ASKED_NOTIFICATION_COUNT = "ASKED_NOTIFICATION_COUNT"
    }
}