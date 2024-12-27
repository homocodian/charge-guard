package com.homocodian.chargeguard.ui.components

import android.app.AlertDialog
import android.content.Context

object ChargingDetectorQSTileDialog {
  fun getDialog(context: Context): AlertDialog {
    val builder = AlertDialog.Builder(context)

    builder.setTitle("Permission Required")

    builder.setMessage(
      "This may be the first time starting the service, " +
        "or the notification permission has not been granted. " +
        "If this is the first time, please start the service from the application. " +
        "Otherwise, check your settings to enable the required permission."
    )

    builder.setPositiveButton("Ok") { dialogInterface, _ ->
      dialogInterface.dismiss()
    }

    return builder.create()
  }
}