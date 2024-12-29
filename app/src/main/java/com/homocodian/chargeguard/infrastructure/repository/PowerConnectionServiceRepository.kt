package com.homocodian.chargeguard.infrastructure.repository

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcast.BootReceiver
import com.homocodian.chargeguard.service.PowerConnectionService
import com.homocodian.chargeguard.util.AlarmScheduler

class PowerConnectionServiceRepository(
  private val applicationContext: Application
) {

  val alarmScheduler = AlarmScheduler(applicationContext)

  val isServiceRunning
    get() = PowerConnectionService.isRunning.value

  val isServiceRunningFlow = PowerConnectionService.isRunning

  fun start(isInvokedFromBg: Boolean = false, enableBootReceiver: Boolean = true) {

    Log.d(TAG, "start: ${this::class.java}")

    if (enableBootReceiver) {
      Log.d(TAG, "start: Enabling boot receiver")
      setBootReceiverState(enabled = true)
    }

    val intent = Intent(applicationContext, PowerConnectionService::class.java).apply {
      action = PowerConnectionService.Action.START.toString()
    }

    if (isInvokedFromBg) {
      alarmScheduler.scheduleImmediateService(intent, 101)
    } else {
      applicationContext.startService(intent)
    }
  }

  fun stop() {
    Log.d(TAG, "start: ${this::class.java}")

    setBootReceiverState(enabled = false)

    Intent(applicationContext, PowerConnectionService::class.java).also {
      applicationContext.stopService(it)
    }
  }

  private fun setBootReceiverState(enabled: Boolean) {
    val component = ComponentName(applicationContext, BootReceiver::class.java)
    val newState = if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED

    applicationContext.packageManager
      .setComponentEnabledSetting(
        component,
        newState,
        PackageManager.DONT_KILL_APP
      )
  }
}