package com.homocodian.chargeguard.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.service.MonitorChargingLevelService
import com.homocodian.chargeguard.util.isServiceRunning

class PowerConnectionReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    when {
      intent.action == Intent.ACTION_POWER_CONNECTED ->
        powerWasConnected(context)

      intent.action == Intent.ACTION_POWER_DISCONNECTED ->
        powerWasDisconnected(context)
    }
  }

  private fun powerWasConnected(context: Context) {
    // Do whatever you need to do when the power is connected here
    Log.d(TAG, "powerWasConnected ")

    if (!isServiceRunning(context, MonitorChargingLevelService::class.java)) {
      Log.d(TAG, "Starting MonitorChargingLevelService")

      Intent(context.applicationContext, MonitorChargingLevelService::class.java).also {
        it.action = MonitorChargingLevelService.Action.START.toString()
        context.startService(it)
      }
    } else {
      Log.d(TAG, "powerWasConnected : MonitorChargingLevelService already running")
    }
  }

  private fun powerWasDisconnected(context: Context) {
    // And here, do whatever you like when the power is disconnected
    Log.d(TAG, "powerWasDisconnected")
    if (isServiceRunning(context, MonitorChargingLevelService::class.java)) {
      Log.d(TAG, "Stopping MonitorChargingLevelService")

      Intent(context.applicationContext, MonitorChargingLevelService::class.java).also {
        it.action = MonitorChargingLevelService.Action.STOP.toString()
        context.startService(it)
      }
    } else {
      Log.d(TAG, "powerWasDisconnected: MonitorChargingLevelService already stopped")
    }
  }

  fun checkCurrentChargingState(context: Context) {
    val batteryStatus: Intent? =
      context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1

    val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
    var isAcCharger: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

    Log.d(TAG, "checkCurrentChargingState: isCharging : $isCharging, chargePlug: $isAcCharger")

    if (isCharging && isAcCharger) {
      powerWasConnected(context) // If the charger is already connected, trigger this immediately
    }
  }
}
