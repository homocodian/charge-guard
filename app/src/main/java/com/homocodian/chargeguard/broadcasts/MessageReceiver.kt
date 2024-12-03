package com.homocodian.chargeguard.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MessageReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    val message = intent?.getStringExtra("message")
    val durationType = intent?.getStringExtra("duration_type") ?: "short"

    if (message != null && context != null) {
      Toast.makeText(
        context.applicationContext,
        message,
        if (durationType.lowercase() == "long") Toast.LENGTH_LONG else Toast.LENGTH_SHORT
      ).show()
    }
  }
}