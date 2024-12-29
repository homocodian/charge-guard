package com.homocodian.chargeguard.util.helper

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.homocodian.chargeguard.ChargingLimitReached
import com.homocodian.chargeguard.R
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.broadcast.NotificationDismissedReceiver
import com.homocodian.chargeguard.constant.AppNotification
import com.homocodian.chargeguard.constant.IntentCode
import kotlin.time.Duration.Companion.minutes

object ChargingLevelHelper {

  fun isLimitReached(intent: Intent, limit: Int): Boolean {
    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    val batteryPct = level * 100 / scale.toFloat()

    return (chargePlug == BatteryManager.BATTERY_PLUGGED_AC
      && status == BatteryManager.BATTERY_STATUS_CHARGING
      && batteryPct >= limit)
  }

  fun showChargingLimitReachedNotification(
    applicationContext: Context,
    mediaPlayer: MediaPlayerHelper
  ) {
    val notificationId = AppNotification.NotificationId.CHARGING_LIMIT_REACHED_NOTIFICATION_ID
    val timeoutDuration = 1.minutes.inWholeMilliseconds

    val notificationManager =
      applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create an Intent to stop the media player
    val stopIntent = Intent(applicationContext, NotificationDismissedReceiver::class.java).apply {
      action = NotificationDismissedReceiver.Actions.STOP.toString()
    }

    val stopPendingIntent: PendingIntent =
      PendingIntent.getBroadcast(
        applicationContext,
        IntentCode.STOP_CHARGING_MONITOR,
        stopIntent,
        PendingIntent.FLAG_IMMUTABLE
      )

    val notificationDismissedIntent =
      Intent(applicationContext, NotificationDismissedReceiver::class.java).apply {
        action = NotificationDismissedReceiver.Actions.Timeout.toString()
      }

    val dismissPendingIntent = PendingIntent.getBroadcast(
      applicationContext.applicationContext,
      AppNotification.NotificationId.CHARGING_LIMIT_REACHED_NOTIFICATION_ID,
      notificationDismissedIntent,
      PendingIntent.FLAG_IMMUTABLE
    )

    // Create the notification with a stop button
    val notification =
      NotificationCompat.Builder(
        applicationContext,
        AppNotification.CHARGING_LIMIT_REACHED_CHANNEL_ID
      ).apply {
        setContentTitle("Charging Limit Reached")
        setContentText("Your device has reached the charging limit.")
        setSmallIcon(R.drawable.ic_launcher_foreground)
        setPriority(NotificationCompat.PRIORITY_HIGH)
        setCategory(NotificationCompat.CATEGORY_ALARM)
        setAutoCancel(true)
        setOngoing(true)
        addAction(R.drawable.ic_launcher_background, "Stop", stopPendingIntent) // Stop button
        setTimeoutAfter(timeoutDuration) // Auto cancel after 1 minutes
        setDeleteIntent(
          dismissPendingIntent
        )
      }

    val fullScreenPendingIntent = PendingIntent.getActivity(
      applicationContext,
      IntentCode.FULL_SCREEN,
      Intent(applicationContext, ChargingLimitReached::class.java),
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
      notificationId, notification.build()
    )

    try {
      if (!mediaPlayer.isPlaying()) {
        mediaPlayer.prepare(applicationContext.applicationContext)
        mediaPlayer.start()
      } else {
        mediaPlayer.reset()
        mediaPlayer.start()
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error starting media player: ", e)
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      NotificationDismissedReceiver.registerTimeoutCallback(applicationContext, timeoutDuration)
    }
  }
}