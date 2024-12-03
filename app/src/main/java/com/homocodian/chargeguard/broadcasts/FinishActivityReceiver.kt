package com.homocodian.chargeguard.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FinishActivityReceiver: BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == "com.example.app.FINISH_ACTIVITY") {
      // Finish the activity after notification has been cancelled
//      finish()
    }
  }
}