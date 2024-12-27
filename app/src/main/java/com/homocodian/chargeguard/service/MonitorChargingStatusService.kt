package com.homocodian.chargeguard.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.service.quicksettings.TileService
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcasts.PowerConnectionReceiver
import com.homocodian.chargeguard.constant.AppNotification
import com.homocodian.chargeguard.infra.repository.ChargingLevelServiceRepository
import com.homocodian.chargeguard.infra.repository.PreferenceDataStoreRepository
import com.homocodian.chargeguard.store.ChargingStatusServiceState
import com.homocodian.chargeguard.util.NotificationCreator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MonitorChargingStatusService : Service() {

  private lateinit var powerConnectionReceiver: PowerConnectionReceiver

  @Inject
  lateinit var preferenceDataStoreRepository: PreferenceDataStoreRepository

  @Inject
  lateinit var chargingLevelServiceRepository: ChargingLevelServiceRepository

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "onCreate: ${this::class.simpleName}")

    powerConnectionReceiver = PowerConnectionReceiver()

    val connectionChangedIntent = IntentFilter().apply {
      addAction(Intent.ACTION_POWER_CONNECTED)
      addAction(Intent.ACTION_POWER_DISCONNECTED)
    }

    registerReceiver(powerConnectionReceiver, connectionChangedIntent)

    powerConnectionReceiver.checkCurrentChargingState(this)

    ChargingStatusServiceState.setState(true)

    TileService.requestListeningState(
      applicationContext,
      ComponentName(applicationContext, ChargingDetectorQSTileService::class.java)
    )
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy: ${this::class.simpleName}")

    unregisterReceiver(powerConnectionReceiver)

    chargingLevelServiceRepository.requestStop()

    ChargingStatusServiceState.setState(false)

    TileService.requestListeningState(
      applicationContext,
      ComponentName(applicationContext, ChargingDetectorQSTileService::class.java)
    )
  }

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d(TAG, "onStartCommand: ${this::class.simpleName}")

    when (intent?.action) {
      MonitorChargingLevelService.Action.START.toString() -> start()
      MonitorChargingLevelService.Action.STOP.toString() -> stop()
    }

    return super.onStartCommand(intent, flags, startId)
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