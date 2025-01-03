package com.homocodian.chargeguard.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.infrastructure.repository.PowerConnectionServiceRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

  @Inject
  lateinit var powerConnectionServiceRepository: PowerConnectionServiceRepository

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      Log.d(TAG, "Boot completed")
      context?.let {
        // boot receiver is already enabled
        powerConnectionServiceRepository.start(
          isInvokedFromBg = true,
          enableBootReceiver = false
        )
      }
    }
  }
}