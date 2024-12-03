package com.homocodian.chargeguard.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.service.MonitorChargingStatusService
import com.homocodian.chargeguard.util.isServiceRunning

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      Log.d(TAG, "Boot completed")
      context?.let {
        if (!isServiceRunning(it, MonitorChargingStatusService::class.java)) {
          val serviceIntent = Intent(it, MonitorChargingStatusService::class.java)
          it.startService(serviceIntent)
          Log.d(TAG, "Started BatteryChargingStatusService")
        } else {
          Log.d(TAG, "BatteryChargingStatusService is already running")
        }
      }
    }
  }
}