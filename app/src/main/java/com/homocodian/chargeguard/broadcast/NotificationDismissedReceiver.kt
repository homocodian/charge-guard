package com.homocodian.chargeguard.broadcast

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.constant.AppNotification
import com.homocodian.chargeguard.service.ChargingLevelService
import com.homocodian.chargeguard.util.helper.NotificationHelper
import kotlinx.coroutines.Runnable

class NotificationDismissedReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      Actions.STOP.toString() -> {
        Log.d(TAG, "${this::class.simpleName} : stopping service")
        stopService(context.applicationContext)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
          removeTimeoutCallback()
        }
      }

      Actions.Timeout.toString() -> {
        Log.d(TAG, "${this::class.simpleName} : stopping service, notification missed")
        stopService(context.applicationContext)
        sendMissedNotification(context.applicationContext)
      }
    }
  }

  companion object {
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    fun registerTimeoutCallback(applicationContext: Context, timeoutDuration: Long) {
      Log.d(TAG, "registerTimeoutCallback: registering handler")

      runnable = Runnable {
        Log.d(TAG, "showChargingLimitReachedNotification: limit reached notification timeout")
        val notificationManager =
          applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(AppNotification.NotificationId.CHARGING_LIMIT_REACHED_NOTIFICATION_ID)
        stopService(applicationContext)
        sendMissedNotification(applicationContext)
      }

      handler = Handler(Looper.getMainLooper())

      handler?.let { h ->
        runnable?.let { r ->
          h.postDelayed(r, timeoutDuration)
        }
      }
    }

    private fun removeTimeoutCallback() {
      Log.d(TAG, "removeTimeoutCallback: start")
      handler?.let { h ->
        runnable?.let { r ->
          Log.d(TAG, "removeTimeoutCallback: removing")
          h.removeCallbacks(r)
        }
      }
      handler = null
      runnable = null
    }

    private fun stopService(applicationContext: Context) {
      applicationContext.stopService(
        Intent(
          applicationContext,
          ChargingLevelService::class.java
        )
      )
    }

    private fun sendMissedNotification(applicationContext: Context) {
      val notification = NotificationHelper.createNotification(
        context = applicationContext,
        channelId = AppNotification.CHARGING_LIMIT_REACHED_CHANNEL_ID,
        title = "Charging limit reached",
        text = "You missed a notification for when your device's charging limit was reached."
      )
      NotificationHelper.notify(
        applicationContext,
        AppNotification.NotificationId.MISSED_LIMIT_REACHED_NOTIFICATION_ID,
        notification
      )
    }
  }

  enum class Actions {
    STOP,
    Timeout
  }
}