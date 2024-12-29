package com.homocodian.chargeguard.ui.components

import android.app.AlertDialog
import android.content.Context
import com.homocodian.chargeguard.R
import com.homocodian.chargeguard.util.openNotificationSettings

object ChargingDetectorQSTileDialog {
  fun getDialog(applicationContext: Context): AlertDialog {
    val builder = AlertDialog.Builder(applicationContext).apply {
      setTitle("Permission Required")
      setMessage(
        "Notification permission is not granted. " +
          "Please enable it in your settings under " +
          "'Apps & Notifications' > ${applicationContext.getString(R.string.app_name)} > Permissions."
      )
      setCancelable(false)
      setPositiveButton("Settings") { dialogInterface, _ ->
        applicationContext.openNotificationSettings()
      }
      setNegativeButton("Cancel") { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
    }

    return builder.create()
  }
}