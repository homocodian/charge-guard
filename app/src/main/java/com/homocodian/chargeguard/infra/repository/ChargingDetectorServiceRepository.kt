package com.homocodian.chargeguard.infra.repository

import android.app.Application
import android.content.Intent
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.domain.repository.ServiceRepository
import com.homocodian.chargeguard.service.MonitorChargingStatusService
import com.homocodian.chargeguard.util.isServiceRunning

class ChargingDetectorServiceRepository(
  private val appContext: Application
) : ServiceRepository {

  override fun start() {
    if (isServiceRunning(appContext, MonitorChargingStatusService::class.java)) {
      Log.d(TAG, "Battery charging Status detector service already running")
      return
    }

    Log.d(TAG, "start: Starting battery charging detector")

    Intent(appContext, MonitorChargingStatusService::class.java).also {
      it.action = MonitorChargingStatusService.Action.START.toString()
      appContext.startService(it)
    }
  }

  override fun stop() {
    if (!isServiceRunning(appContext, MonitorChargingStatusService::class.java)) {
      Log.d(TAG, "Battery charging Status detector service already stopped")
      return
    }

    Log.d(TAG, "Stopping battery charging detector")

    Intent(appContext, MonitorChargingStatusService::class.java).also {
      it.action = MonitorChargingStatusService.Action.STOP.toString()
      appContext.startService(it)
    }
  }
}