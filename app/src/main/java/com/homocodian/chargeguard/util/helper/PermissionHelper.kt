package com.homocodian.chargeguard.util.helper

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class PermissionHelper(
  private val applicationContext: Application
) {
  fun hasNecessaryPermissions(permission: Permission): Boolean {
    if (permission.versionCode != null) {
      if (Build.VERSION.SDK_INT >= permission.versionCode) {
        if (ContextCompat.checkSelfPermission(
            applicationContext,
            permission.permission
          ) != PackageManager.PERMISSION_GRANTED
        ) {
          return false
        }
      }
    }
    return true
  }

  data class Permission(val permission: String, val versionCode: Int?)
}
