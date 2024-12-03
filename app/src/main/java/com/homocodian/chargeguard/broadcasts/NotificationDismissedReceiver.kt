package com.homocodian.chargeguard.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.service.MonitorChargingLevelService

class NotificationDismissedReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {

    if (intent.action == Actions.STOP.toString()) {

      Log.d(TAG, "Dismissing monitoring charging level service")

      context.applicationContext.stopService(
        Intent(
          context,
          MonitorChargingLevelService::class.java
        )
      )

    }
  }

  enum class Actions {
    STOP
  }
}