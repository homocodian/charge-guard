package com.homocodian.chargeguard.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcast.ChargingLevelChangeReceiver
import com.homocodian.chargeguard.constant.AppNotification
import com.homocodian.chargeguard.constant.BatteryLevel
import com.homocodian.chargeguard.infrastructure.repository.PreferenceDataStoreRepository
import com.homocodian.chargeguard.util.helper.NotificationHelper
import com.homocodian.chargeguard.util.helper.MediaPlayerHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ChargingLevelService : Service() {

  private var chargingLevelChangeReceiver: ChargingLevelChangeReceiver? = null

  private val scope = CoroutineScope(Dispatchers.Main + Job())

  @Inject
  lateinit var preferenceDataStoreRepository: PreferenceDataStoreRepository

  override fun onCreate() {
    super.onCreate()

    scope.launch {
      val chargingLevelToMonitor = withContext(Dispatchers.IO) {
        return@withContext preferenceDataStoreRepository.getInt(BatteryLevel.DataStore.BATTERY_LEVEL_TO_MONITOR_KEY)
          ?: BatteryLevel.DEFAULT_BATTERY_LEVEL_TO_MONITOR
      }

      Log.d(TAG, "onCreate: Battery level $chargingLevelToMonitor")

      ensureActive()

      chargingLevelChangeReceiver = ChargingLevelChangeReceiver(chargingLevelToMonitor)

      registerReceiver(chargingLevelChangeReceiver, IntentFilter().apply {
        addAction(Intent.ACTION_BATTERY_CHANGED)
      })

      startForeground()
    }
  }

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy: MonitorChargingLevelService")

    scope.cancel()

    (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(AppNotification.NotificationId.CHARGING_LIMIT_REACHED_NOTIFICATION_ID)

    MediaPlayerHelper.release()

    // finish full screen intent
    val finishIntent = Intent(this.packageName + ".FINISH_ACTIVITY")
    this.applicationContext.sendBroadcast(finishIntent)

    if (chargingLevelChangeReceiver != null) {
      unregisterReceiver(chargingLevelChangeReceiver)
    }
  }

  private fun startForeground() {
    val notification = NotificationHelper.createNotification(
      context = this.applicationContext,
      channelId = AppNotification.CHARGING_LEVEL_MONITOR_CHANNEL_ID,
      title = "Charging Monitor Active",
      text = "Actively monitoring your charging status"
    )

    startForeground(AppNotification.Foreground.CHARGING_LEVEL_SERVICE_ID, notification)
  }
}