package com.homocodian.chargeguard.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.infrastructure.repository.ChargingLevelServiceRepository
import com.homocodian.chargeguard.service.ChargingLevelService
import javax.inject.Inject

class PowerConnectionReceiver : BroadcastReceiver() {

  @Inject
  lateinit var chargingLevelServiceRepository: ChargingLevelServiceRepository

  override fun onReceive(context: Context, intent: Intent) {
    when {
      intent.action == Intent.ACTION_POWER_CONNECTED ->
        powerWasConnected(context.applicationContext)

      intent.action == Intent.ACTION_POWER_DISCONNECTED ->
        powerWasDisconnected(context.applicationContext)
    }
  }

  private fun powerWasConnected(context: Context) {
    // Do whatever you need to do when the power is connected here
    Log.d(TAG, "powerWasConnected ")
    Intent(context, ChargingLevelService::class.java).also {
      context.startService(it)
    }
  }

  private fun powerWasDisconnected(context: Context) {
    // And here, do whatever you like when the power is disconnected
    Log.d(TAG, "powerWasDisconnected")
    Intent(context, ChargingLevelService::class.java).also {
      context.stopService(it)
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
