package com.homocodian.chargeguard.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcasts.ChargingLevelChangeReceiver
import com.homocodian.chargeguard.constant.AppNotification
import com.homocodian.chargeguard.constant.BatteryLevel
import com.homocodian.chargeguard.infra.repository.PreferenceDataStoreRepository
import com.homocodian.chargeguard.util.NotificationCreator
import com.homocodian.chargeguard.util.helper.MediaPlayerHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MonitorChargingLevelService : Service() {

  private var chargingLevelChangeReceiver: ChargingLevelChangeReceiver? = null
  private var job: Job? = null

  @Inject
  lateinit var preferenceDataStoreRepository: PreferenceDataStoreRepository

  override fun onCreate() {
    super.onCreate()

    job = CoroutineScope(Dispatchers.IO).launch {
      val chargingLevelToMonitor =
        preferenceDataStoreRepository.getInt(BatteryLevel.DataStore.BATTERY_LEVEL_TO_MONITOR_KEY)
          ?: BatteryLevel.DEFAULT_BATTERY_LEVEL_TO_MONITOR

      Log.d(TAG, "onCreate: Battery level $chargingLevelToMonitor")

      chargingLevelChangeReceiver = ChargingLevelChangeReceiver(chargingLevelToMonitor)

      ensureActive()

      registerReceiver(chargingLevelChangeReceiver, IntentFilter().apply {
        addAction(Intent.ACTION_BATTERY_CHANGED)
      })
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

    when (intent?.action) {
      Action.START.toString() -> start()
      Action.STOP.toString() -> stop()
      Action.RESTART.toString() -> restart()
    }

    return super.onStartCommand(intent, flags, startId)
  }

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy: MonitorChargingLevelService")

    job?.cancel()

    (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(AppNotification.NotificationId.CHARGING_LIMIT_REACHED_NOTIFICATION_ID)

    MediaPlayerHelper.release()

    // finish full screen intent
    val finishIntent = Intent(this.packageName + ".FINISH_ACTIVITY")
    this.applicationContext.sendBroadcast(finishIntent)

    if (chargingLevelChangeReceiver != null) {
      unregisterReceiver(chargingLevelChangeReceiver)
    }
  }

  enum class Action {
    STOP, START, RESTART
  }

  fun start() {
    val notification = NotificationCreator.create(
      context = this.applicationContext,
      channelId = AppNotification.BATTERY_LEVEL_MONITOR_CHANNEL_ID,
      title = "Charging Monitor Active",
      text = "Actively monitoring your charging status"
    )

    startForeground(AppNotification.Foreground.MONITOR_CHARGING_LEVEL_SERVICE_ID, notification)
  }

  fun stop() {
    stopSelf()
  }

  fun restart() {
    stop()

    val serviceIntent = Intent(applicationContext, MonitorChargingLevelService::class.java)
    serviceIntent.action = Action.START.toString() // Start action for the service
    startService(serviceIntent)
  }

}