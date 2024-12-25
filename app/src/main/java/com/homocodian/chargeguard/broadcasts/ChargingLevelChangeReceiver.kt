package com.homocodian.chargeguard.broadcasts

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.homocodian.chargeguard.ChargingLimitReached
import com.homocodian.chargeguard.R
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.constant.AppNotification
import com.homocodian.chargeguard.constant.IntentCode
import com.homocodian.chargeguard.util.helper.MediaPlayerHelper
import com.homocodian.chargeguard.util.isNotificationShowing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class ChargingLevelChangeReceiver(private val batteryChargingLevelToMonitor: Int) :
  BroadcastReceiver() {

  private var job: Job? = null
  var isNotificationAlreadyShown = false

  override fun onReceive(context: Context, intent: Intent) {
    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    val batteryPct = level * 100 / scale.toFloat()

    if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC && status == BatteryManager.BATTERY_STATUS_CHARGING && batteryPct >= batteryChargingLevelToMonitor) {
      Log.d(TAG, "AC charger is plugged in")

      if (isNotificationAlreadyShown) {
        Log.d(TAG, "Limit reached notification is already shown")
        return
      }

      Log.d(TAG, "AC charger is plugged in, showing notification")

      if (job?.isActive == true) {
        job?.cancel() // cancel previous job if it's still running
      }

      job = CoroutineScope(Dispatchers.Default).launch {
        Log.d(TAG, "Coroutine started")
        if (!isNotificationShowing(
            context.applicationContext,
            AppNotification.NotificationId.CHARGING_LIMIT_REACHED_NOTIFICATION_ID
          )
        ) {
          ensureActive()
          showFullScreenNotification(context)
          isNotificationAlreadyShown = true
        }
        Log.d(TAG, "Coroutine finished")
      }
    }
  }

  fun showFullScreenNotification(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create an Intent to stop the media player
    val stopIntent = Intent(context, NotificationDismissedReceiver::class.java).apply {
      action = NotificationDismissedReceiver.Actions.STOP.toString()
    }

    val stopPendingIntent: PendingIntent =
      PendingIntent.getBroadcast(
        context,
        IntentCode.STOP_CHARGING_MONITOR,
        stopIntent,
        PendingIntent.FLAG_IMMUTABLE
      )

    // Create the notification with a stop button
    val notification =
      NotificationCompat.Builder(context, AppNotification.CHARGING_LIMIT_REACHED_CHANNEL_ID)
        .setContentTitle("Charging Limit Reached")
        .setContentText("Your device has reached the charging limit.")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setAutoCancel(true)
        .setOngoing(true)
        .addAction(R.drawable.ic_launcher_background, "Stop", stopPendingIntent) // Stop button
        .setTimeoutAfter(1.minutes.inWholeMilliseconds) // Auto cancel after 1 minutes
        .setDeleteIntent(
          createOnDismissedIntent(
            context,
            NotificationDismissedReceiver.Actions.STOP.toString()
          )
        )

    val fullScreenPendingIntent = PendingIntent.getActivity(
      context,
      IntentCode.FULL_SCREEN,
      Intent(context, ChargingLimitReached::class.java),
      PendingIntent.FLAG_IMMUTABLE
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      if (notificationManager.canUseFullScreenIntent()) {
        notification.setFullScreenIntent(fullScreenPendingIntent, true)
      }
    } else {
      notification.setFullScreenIntent(fullScreenPendingIntent, true)
    }

    // Show the notification
    notificationManager.notify(
      AppNotification.NotificationId.CHARGING_LIMIT_REACHED_NOTIFICATION_ID, notification.build()
    )

    try {
      if (!MediaPlayerHelper.isPlaying()) {
        MediaPlayerHelper.prepare(context.applicationContext)
        MediaPlayerHelper.start()
      } else {
        MediaPlayerHelper.reset()
        MediaPlayerHelper.start()
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error starting media player: ", e)
    }
  }

  private fun createOnDismissedIntent(
    context: Context, intentAction: String
  ): PendingIntent? {
    val notificationDismissedIntent =
      Intent(context, NotificationDismissedReceiver::class.java).apply {
        action = intentAction
      }

    val pendingIntent = PendingIntent.getBroadcast(
      context.applicationContext,
      AppNotification.NotificationId.CHARGING_LIMIT_REACHED_NOTIFICATION_ID,
      notificationDismissedIntent,
      PendingIntent.FLAG_IMMUTABLE
    )

    return pendingIntent
  }

}