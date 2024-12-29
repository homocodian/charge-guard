package com.homocodian.chargeguard.util.helper

import android.content.ComponentName
import android.content.Context
import android.service.quicksettings.TileService
import com.homocodian.chargeguard.service.ChargingMonitorTileService

object TileHelper {
  fun requestTileListeningState(context: Context) {
    TileService.requestListeningState(
      context,
      ComponentName(
        context,
        ChargingMonitorTileService::class.java
      )
    )
  }
}