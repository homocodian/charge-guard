package com.homocodian.chargeguard.util

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.homocodian.chargeguard.TAG

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
  val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

  val runningServices = manager.getRunningServices(Int.MAX_VALUE)

  for (service in runningServices) {
    Log.d(TAG, "service name : ${service.service.className}")
    if (service.service.className == serviceClass.name) {
      return true
    }
  }

  return false
}