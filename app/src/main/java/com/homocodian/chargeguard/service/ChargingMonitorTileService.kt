package com.homocodian.chargeguard.service

import android.Manifest
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.infrastructure.repository.PowerConnectionServiceRepository
import com.homocodian.chargeguard.ui.components.ChargingDetectorQSTileDialog
import com.homocodian.chargeguard.util.helper.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChargingMonitorTileService : TileService() {

  @Inject
  lateinit var powerConnectionServiceRepository: PowerConnectionServiceRepository

  @Inject
  lateinit var permissionHelper: PermissionHelper

  private var shouldSkipOnClick = false

  override fun onStartListening() {
    super.onStartListening()
    Log.d(TAG, "onStartListening ${powerConnectionServiceRepository.isServiceRunning}")
    updateTile()
  }

  override fun onClick() {
    super.onClick()
    Log.d(TAG, "onClick: ${this::class.simpleName}")

    val tile = getTile()

    if (tile == null) return

    val tileState = tile.state

    if (shouldSkipOnClick) {
      Log.d(TAG, "onClick: skipping tile onClick")
      return
    }

    shouldSkipOnClick = true

    var newState = tileState

    Log.d(
      TAG,
      "${this::class.simpleName} clicked : " +
        "${powerConnectionServiceRepository.isServiceRunning}"
    )

    if (tileState == Tile.STATE_ACTIVE) {
      powerConnectionServiceRepository.stop()
      newState = Tile.STATE_INACTIVE
    } else if (tileState == Tile.STATE_INACTIVE) {
      if (!permissionHelper.hasNecessaryPermissions(
          PermissionHelper.Permission(
            permission = Manifest.permission.POST_NOTIFICATIONS,
            versionCode = Build.VERSION_CODES.TIRAMISU
          )
        )
      ) {
        Log.d(TAG, "permission not granted")
        showDialog(ChargingDetectorQSTileDialog.getDialog(this.applicationContext))
        shouldSkipOnClick = false
        newState = Tile.STATE_INACTIVE
        return
      } else {
        powerConnectionServiceRepository.start(isInvokedFromBg = true)
        newState = Tile.STATE_ACTIVE
      }
    }

    Log.d(TAG, "onClick: New Tile state $newState")
    updateTile(newState)
    shouldSkipOnClick = false
    Log.d(TAG, "onClick: tile onClicked completed")
  }

  private fun updateTile(state: Int? = null) {
    val tile = qsTile

    if (tile == null) {
      Log.d(TAG, "updateTile: null")
      return
    }

    tile.state = if (state != null) {
      state
    } else if (powerConnectionServiceRepository.isServiceRunning) {
      Tile.STATE_ACTIVE
    } else {
      Tile.STATE_INACTIVE
    }

    tile.updateTile()
  }

  private fun getTile(): Tile? {
    val tile = qsTile
    return tile
  }
}