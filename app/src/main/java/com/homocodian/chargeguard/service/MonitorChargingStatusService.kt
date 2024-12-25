package com.homocodian.chargeguard.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcasts.PowerConnectionReceiver
import com.homocodian.chargeguard.constant.AppNotification
import com.homocodian.chargeguard.util.NotificationCreator
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
  }

  override fun onDestroy() {
    super.onDestroy()

    Log.d(TAG, "Stopping monitor service")

    unregisterReceiver(powerConnectionReceiver)

    stopChargingLevelMonitoringService()
  }

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

    when (intent?.action) {
      MonitorChargingLevelService.Action.START.toString() -> start()
      MonitorChargingLevelService.Action.STOP.toString() -> stop()
    }

    return super.onStartCommand(intent, flags, startId)
  }

  private fun stopChargingLevelMonitoringService() {
    if (isServiceRunning(this, MonitorChargingLevelService::class.java)) {
      Intent(this, MonitorChargingLevelService::class.java).also {
        stopService(it)
      }
    }
  }

  enum class Action {
    START, STOP
  }

  fun start() {
    val notification = NotificationCreator.create(
      context = this.applicationContext,
      channelId = AppNotification.BATTERY_POWER_CONNECTION_MONITOR_CHANNEL_ID,
      title = "Charging Detector Active",
      text = "Actively monitoring your power connection"
    )

    startForeground(AppNotification.Foreground.MONITOR_CHARGING_STATUS_SERVICE_ID, notification)
  }

  fun stop() {
    stopSelf()
  }
}