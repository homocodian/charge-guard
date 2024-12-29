package com.homocodian.chargeguard.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.homocodian.chargeguard.TAG

@SuppressLint("ServiceCast")
class AlarmScheduler(
  private val context: Context
) {
  private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

  @SuppressLint("MissingPermission")
  fun scheduleImmediateService(intent: Intent, requestCode: Int) {
    Log.d(TAG, "scheduleImmediateService: ${this::class.java}")

    val alarmManager = getAlarmManager()

    // Intent to start the service
    val pendingIntent = getPendingIntent(requestCode, intent)

    // Set an immediate alarm
    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      System.currentTimeMillis(),
      pendingIntent
    )
  }

  private fun getPendingIntent(requestCode: Int, intent: Intent): PendingIntent {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      return PendingIntent.getForegroundService(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
      )
    }
    return PendingIntent.getService(
      context,
      requestCode,
      intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
  }

  fun getAlarmManager(): AlarmManager {
    return alarmManager
      ?: throw IllegalStateException("AlarmManager is not available on this device.")
  }
}