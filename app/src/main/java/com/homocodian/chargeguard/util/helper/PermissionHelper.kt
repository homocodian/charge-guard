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

  fun hasNecessaryPermissions(permissions: List<Permission>): Boolean {
    permissions.forEach { permission ->
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
    }
    return true
  }

  data class Permission(val permission: String, val versionCode: Int?) {
    init {
      // Validate versionCode to make sure it matches a valid Build.VERSION_CODES constant
      if (versionCode != null && !isValidVersionCode(versionCode)) {
        throw IllegalArgumentException("Invalid versionCode: $versionCode. It should be a valid Build.VERSION_CODES value.")
      }
    }

    private fun isValidVersionCode(versionCode: Int): Boolean {
      return versionCode in Build.VERSION_CODES::class.java.fields
        .map { it.getInt(null) }
        .toSet()
    }
  }
}