package com.homocodian.chargeguard.infra.repository

import android.app.Application
import android.content.Intent
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.domain.repository.ServiceRepository
import com.homocodian.chargeguard.service.MonitorChargingStatusService
import com.homocodian.chargeguard.util.isServiceRunning

class ChargingStatusServiceRepository(
  private val appContext: Application
) : ServiceRepository {

  override fun requestStart() {
    if (isServiceRunning(appContext, MonitorChargingStatusService::class.java)) {
      Log.d(TAG, "requestStart: Battery charging Status detector service already running")
      return
    }

    Log.d(TAG, "requestStart: Starting battery charging detector")

    Intent(appContext, MonitorChargingStatusService::class.java).also {
      it.action = MonitorChargingStatusService.Action.START.toString()
      appContext.startService(it)
    }
  }

  override fun requestStop() {
    if (!isServiceRunning(appContext, MonitorChargingStatusService::class.java)) {
      Log.d(TAG, "requestStop: Battery charging Status detector service already stopped")
      return
    }

    Log.d(TAG, "requestStop: Stopping battery charging detector")

    Intent(appContext, MonitorChargingStatusService::class.java).also {
      it.action = MonitorChargingStatusService.Action.STOP.toString()
      appContext.startService(it)
    }
  }

  override fun start() {
    Log.d(TAG, "start: ${this::class.simpleName}")
    Intent(appContext, MonitorChargingStatusService::class.java).also {
      it.action = MonitorChargingStatusService.Action.START.toString()
      appContext.startService(it)
    }
  }

  override fun stop() {
    Log.d(TAG, "stop: ${this::class.simpleName}")
    Intent(appContext, MonitorChargingStatusService::class.java).also {
      it.action = MonitorChargingStatusService.Action.STOP.toString()
      appContext.startService(it)
    }
  }
}