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

      Log.d(TAG, "onCreate: Batter level $chargingLevelToMonitor")

      chargingLevelChangeReceiver = ChargingLevelChangeReceiver(chargingLevelToMonitor)

      ensureActive()


      registerReceiver(chargingLevelChangeReceiver, IntentFilter().apply {
        addAction(Intent.ACTION_BATTERY_CHANGED)
      })
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
}