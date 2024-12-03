package com.homocodian.chargeguard.util.helper

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.provider.Settings
import android.util.Log
import com.homocodian.chargeguard.TAG

object MediaPlayerHelper {

  private var mediaPlayer: MediaPlayer? = null
//  private var vibrator: Vibrator? = null

  fun prepare(context: Context) {

    if (!isApplicationContext(context)) {
      throw IllegalArgumentException("Passed context is not a application context")
    }

    mediaPlayer = MediaPlayer().apply {
      setAudioAttributes(
        AudioAttributes.Builder()
          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .setUsage(AudioAttributes.USAGE_ALARM)
          .build(),
      )
      isLooping = true
      setDataSource(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
      prepare()
    }

//    vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//      val vibratorManager =
//        applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
//      vibratorManager.defaultVibrator
//    } else {
//      @Suppress("DEPRECATION")
//      applicationContext.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
//    }
  }

  fun isPlaying(): Boolean {
    Log.d(TAG, "isPlaying: MedialPlayer: ${mediaPlayer?.isPlaying}")
    return mediaPlayer?.isPlaying == true
  }

  fun start() {
    Log.d(TAG, "start: media player")
    mediaPlayer?.start()
//    val pattern = longArrayOf(0, 500, 1000)
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//      vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
//    } else {
//      vibrator?.vibrate(pattern, 0)
//    }
  }
  
  fun reset() {
    Log.d(TAG, "reset: media player")
    mediaPlayer?.reset()
  }

  fun release() {
    Log.d(TAG, "release: MediaPlayer")
    mediaPlayer?.release()
//    vibrator?.cancel()
    mediaPlayer = null
//    vibrator = null
  }

  fun isApplicationContext(context: Context): Boolean {
    return context.applicationContext == context
  }
}