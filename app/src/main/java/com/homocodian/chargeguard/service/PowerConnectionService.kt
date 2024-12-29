package com.homocodian.chargeguard.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcast.PowerConnectionReceiver
import com.homocodian.chargeguard.constant.AppNotification
import com.homocodian.chargeguard.infrastructure.repository.ChargingLevelServiceRepository
import com.homocodian.chargeguard.infrastructure.repository.PreferenceDataStoreRepository
import com.homocodian.chargeguard.util.helper.NotificationHelper
import com.homocodian.chargeguard.util.helper.TileHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class PowerConnectionService : Service() {
  private lateinit var powerConnectionReceiver: PowerConnectionReceiver

  @Inject
  lateinit var preferenceDataStoreRepository: PreferenceDataStoreRepository

  @Inject
  lateinit var chargingLevelServiceRepository: ChargingLevelServiceRepository

  companion object {
    private var _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()
  }

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "onCreate: ${this::class.simpleName}")

    registerPowerConnectionReceiver()
    powerConnectionReceiver.checkCurrentChargingState(applicationContext)

    _isRunning.value = true
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d(TAG, "onStartCommand: ${intent?.action}")
    if (intent == null) { // likely a restart
      startForeground()
    } else {
      when (intent.action) {
        Action.START.toString() -> startForeground()
        Action.STOP.toString() -> stopSelf()
      }
    }
    TileHelper.requestTileListeningState(applicationContext)
    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy: ${this::class.simpleName}")

    unregisterReceiver(powerConnectionReceiver)

    chargingLevelServiceRepository.stop()

    _isRunning.value = false

    TileHelper.requestTileListeningState(applicationContext)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private fun registerPowerConnectionReceiver() {
    powerConnectionReceiver = PowerConnectionReceiver()

    val connectionChangedIntent = IntentFilter().apply {
      addAction(Intent.ACTION_POWER_CONNECTED)
      addAction(Intent.ACTION_POWER_DISCONNECTED)
    }

    registerReceiver(powerConnectionReceiver, connectionChangedIntent)
  }

  private fun startForeground() {
    val notification = NotificationHelper.createNotification(
      context = this.applicationContext,
      channelId = AppNotification.POWER_CONNECTION_MONITOR_CHANNEL_ID,
      title = "Charging Detector Active",
      text = "Actively monitoring your power connection"
    )

    startForeground(AppNotification.Foreground.POWER_CONNECTION_SERVICE_ID, notification)
    Log.d(TAG, "startForeground: ${this::class.simpleName}")
  }

  enum class Action {
    STOP, START
  }
}