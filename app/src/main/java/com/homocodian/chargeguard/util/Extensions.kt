package com.homocodian.chargeguard.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

internal fun Context.openAppSettings() {
  Intent(
    Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts(
      "package", packageName, null
    )
  ).also(::startActivity)
}

internal fun Context.openNotificationSettings() {
  (Intent().apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
      putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    } else {
      action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
      data = Uri.parse("package:${packageName}")
    }
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }).also(::startActivity)
}

internal fun Context.findActivity(): Activity {
  var context = this
  while (context is ContextWrapper) {
    if (context is Activity) return context
    context = context.baseContext
  }
  throw IllegalStateException("Permissions should be called in the context of an Activity")
}