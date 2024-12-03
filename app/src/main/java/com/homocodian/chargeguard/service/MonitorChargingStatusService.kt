package com.homocodian.chargeguard.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcasts.PowerConnectionReceiver
import com.homocodian.chargeguard.util.isServiceRunning

class MonitorChargingStatusService : Service() {

  private lateinit var powerConnectionReceiver: PowerConnectionReceiver

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "Starting monitor service")

    powerConnectionReceiver = PowerConnectionReceiver()

    val connectionChangedIntent = IntentFilter().apply {
      addAction(Intent.ACTION_POWER_CONNECTED)
      addAction(Intent.ACTION_POWER_DISCONNECTED)
    }

    registerReceiver(powerConnectionReceiver, connectionChangedIntent)

    powerConnectionReceiver.checkCurrentChargingState(this)

    val intent = Intent(this.applicationContext.packageName + ".MESSAGE").apply {
      putExtra("message", "Charging Detector started")
      putExtra("duration_type", "short")
    }
    this.applicationContext.sendBroadcast(intent)
  }

  override fun onDestroy() {
    super.onDestroy()

    Log.d(TAG, "Stopping monitor service")

    unregisterReceiver(powerConnectionReceiver)

    stopChargingLevelMonitoringService()

    val intent = Intent(this.applicationContext.packageName + ".MESSAGE").apply {
      putExtra("message", "Charging Detector Stopped")
      putExtra("duration_type", "short")
    }

    this.applicationContext.sendBroadcast(intent)
  }

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    return super.onStartCommand(intent, flags, startId)
  }

  private fun stopChargingLevelMonitoringService() {
    if (isServiceRunning(this, MonitorChargingLevelService::class.java)) {
      Intent(this, MonitorChargingLevelService::class.java).also {
        stopService(it)
      }
    }
  }
}