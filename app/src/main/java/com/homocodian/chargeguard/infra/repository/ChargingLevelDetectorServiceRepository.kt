package com.homocodian.chargeguard.infra.repository

import android.app.Application
import android.content.Intent
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.domain.repository.ServiceRepository
import com.homocodian.chargeguard.service.MonitorChargingLevelService
import com.homocodian.chargeguard.util.isServiceRunning
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChargingLevelDetectorServiceRepository @Inject constructor(
  @ApplicationContext private val appContext: Application
) : ServiceRepository {

  init {
    Log.d(TAG, "ChargingLevelDetectorServiceRepository init")
  }

  override fun start() {
    if (isServiceRunning(appContext, MonitorChargingLevelService::class.java)) {
      Log.d(TAG, "Battery charging level detector service already running")
      return
    }

    Intent(appContext, MonitorChargingLevelService::class.java).also {
      it.action = MonitorChargingLevelService.Action.STOP.toString()
      appContext.startService(it)
    }
  }

  override fun stop() {
    if (!isServiceRunning(appContext, MonitorChargingLevelService::class.java)) {
      Log.d(TAG, "Battery charging level detector service already stopped")
      return
    }

    Intent(appContext, MonitorChargingLevelService::class.java).also {
      it.action = MonitorChargingLevelService.Action.STOP.toString()
      appContext.startService(it)
    }
  }

  fun restartIfRunning() {
    if (!isServiceRunning(appContext, MonitorChargingLevelService::class.java)) {
      Log.d(TAG, "restartIfRunning : Battery charging level detector service already stopped")
      return
    }

    Intent(appContext, MonitorChargingLevelService::class.java).also {
      it.action = MonitorChargingLevelService.Action.RESTART.toString()
      appContext.startService(it)
    }
  }

}