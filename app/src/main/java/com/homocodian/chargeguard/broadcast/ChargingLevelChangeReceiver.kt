package com.homocodian.chargeguard.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.homocodian.chargeguard.util.helper.ChargingLevelHelper
import com.homocodian.chargeguard.util.helper.MediaPlayerHelper

class ChargingLevelChangeReceiver(private val batteryChargingLevelToMonitor: Int) :
  BroadcastReceiver() {

  var isNotificationAlreadyShown = false

  override fun onReceive(context: Context, intent: Intent) {
    if (!isNotificationAlreadyShown &&
      ChargingLevelHelper.isLimitReached(intent, batteryChargingLevelToMonitor)
      && NotificationManagerCompat.from(context.applicationContext).areNotificationsEnabled()
    ) {
      ChargingLevelHelper.showChargingLimitReachedNotification(context.applicationContext, MediaPlayerHelper)
      isNotificationAlreadyShown = true
    }
  }
}