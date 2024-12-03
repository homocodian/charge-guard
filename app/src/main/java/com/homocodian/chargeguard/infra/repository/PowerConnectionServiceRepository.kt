package com.homocodian.chargeguard.infra.repository

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcasts.BootReceiver

class PowerConnectionServiceRepository(
  private val appContext: Application,
  private val chargingDetectorServiceRepository: ChargingDetectorServiceRepository
) {

  init {
    Log.d(TAG, "PowerConnectionServiceRepository class is initialized")
  }

  fun start() {
    val component = ComponentName(appContext, BootReceiver::class.java)

    Log.d(TAG, "${appContext.packageManager.getComponentEnabledSetting(component)}")

    appContext.packageManager
      .setComponentEnabledSetting(
        component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
      )

    Log.d(TAG, "${appContext.packageManager.getComponentEnabledSetting(component)}")

    chargingDetectorServiceRepository.start()
  }

  fun stop() {
    val component = ComponentName(appContext, BootReceiver::class.java)

    Log.d(TAG, "${appContext.packageManager.getComponentEnabledSetting(component)}")

    appContext.packageManager
      .setComponentEnabledSetting(
        component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
      )

    Log.d(TAG, "${appContext.packageManager.getComponentEnabledSetting(component)}")

    chargingDetectorServiceRepository.stop()
  }
}