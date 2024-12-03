package com.homocodian.chargeguard.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Activity.openAppSettings() {
  Intent(
    Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts(
      "package", packageName, null
    )
  ).also(::startActivity)
}

internal fun Context.findActivity(): Activity {
  var context = this
  while (context is ContextWrapper) {
    if (context is Activity) return context
    context = context.baseContext
  }
  throw IllegalStateException("Permissions should be called in the context of an Activity")
}