package com.homocodian.chargeguard.infra.repository

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcasts.BootReceiver
import com.homocodian.chargeguard.service.MonitorChargingStatusService
import com.homocodian.chargeguard.util.isServiceRunning

class PowerConnectionServiceRepository(
  private val appContext: Application,
  private val chargingStatusServiceRepository: ChargingStatusServiceRepository
) {

  fun start() {
    val isBootReceiverEnabled = isBootReceiverEnabled()

    Log.d(TAG, "start: isBootReceiverEnabled = $isBootReceiverEnabled")

    if (!isBootReceiverEnabled) {
      val component = ComponentName(appContext, BootReceiver::class.java)

      appContext.packageManager
        .setComponentEnabledSetting(
          component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
          PackageManager.DONT_KILL_APP
        )

      Log.d(TAG, "start: isBootReceiverEnabled = ${isBootReceiverEnabled()}")
    }

    chargingStatusServiceRepository.requestStart()
  }

  fun stop() {

    val isBootReceiverEnabled = isBootReceiverEnabled()

    Log.d(TAG, "stop: isBootReceiverEnabled = $isBootReceiverEnabled")

    if (isBootReceiverEnabled) {
      val component = ComponentName(appContext, BootReceiver::class.java)

      appContext.packageManager
        .setComponentEnabledSetting(
          component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
          PackageManager.DONT_KILL_APP
        )

      Log.d(TAG, "stop: isBootReceiverEnabled = ${isBootReceiverEnabled()}")
    }

    chargingStatusServiceRepository.requestStop()
  }

  fun isPowerConnectionServiceRunning(): Boolean {
    return isServiceRunning(appContext, MonitorChargingStatusService::class.java)
  }

  private fun isBootReceiverEnabled(): Boolean {
    val packageManager = appContext.packageManager
    val componentName = ComponentName(appContext, BootReceiver::class.java)

    val state = packageManager.getComponentEnabledSetting(componentName)
    return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
  }
}