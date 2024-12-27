package com.homocodian.chargeguard.service

import android.Manifest
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.infra.repository.ChargingStatusServiceRepository
import com.homocodian.chargeguard.store.ChargingStatusServiceState
import com.homocodian.chargeguard.ui.components.ChargingDetectorQSTileDialog
import com.homocodian.chargeguard.util.helper.PermissionHelper
import com.homocodian.chargeguard.util.isServiceRunning
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChargingDetectorQSTileService : TileService() {

  @Inject
  lateinit var chargingStatusServiceRepository: ChargingStatusServiceRepository

  @Inject
  lateinit var permissionHelper: PermissionHelper

  override fun onTileAdded() {
    super.onTileAdded()

    Log.d(TAG, "onTileAdded: ${this::class.simpleName}")

    qsTile.apply {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        subtitle = "Start"
      }
      updateTile()
    }
  }

  override fun onStartListening() {
    super.onStartListening()
    Log.d(TAG, "onStartListening: ")

    val isRunning = ChargingStatusServiceState.state.value

    Log.d(TAG, "onStartListening (state): $isRunning")

    updateTileState(if (isRunning) State.STATE_ON else State.STATE_OFF)
  }

  override fun onStopListening() {
    super.onStopListening()
    Log.d(TAG, "onStopListening: ")
  }

  override fun onClick() {
    super.onClick()

    updateTileState(if (qsTile.state == Tile.STATE_ACTIVE) State.STATE_ON else State.STATE_OFF)

    Log.d(TAG, "${this::class.simpleName} clicked")

    if (!permissionHelper.hasNecessaryPermissions(
        PermissionHelper.Permission(
          permission = Manifest.permission.POST_NOTIFICATIONS,
          versionCode = Build.VERSION_CODES.TIRAMISU
        )
      )
    ) {
      if (qsTile.state == Tile.STATE_ACTIVE) {
        updateTileState(State.STATE_OFF)
      }
      showDialog(ChargingDetectorQSTileDialog.getDialog(this))
      return
    }

    if (isServiceRunning(this.applicationContext, MonitorChargingStatusService::class.java)) {
      Log.d(TAG, "stopping : ${MonitorChargingStatusService::class.simpleName}")

      updateTileState(State.STATE_OFF)

      chargingStatusServiceRepository.stop()
    } else {
      Log.d(TAG, "starting : ${MonitorChargingStatusService::class.simpleName}")

      updateTileState(State.STATE_ON)

      chargingStatusServiceRepository.start()
    }
  }

  override fun onTileRemoved() {
    super.onTileRemoved()

    Log.d(TAG, "onTileRemoved: ${this::class.simpleName}")

    qsTile.apply {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        subtitle = null
      }
      updateTile()
    }
  }

  private fun updateTileState(state: State) {
    qsTile.also {
      when (state) {
        State.STATE_ON -> {
          it.state = Tile.STATE_ACTIVE
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            it.subtitle = "Stop"
          }
        }

        State.STATE_OFF -> {
          it.state = Tile.STATE_INACTIVE
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            it.subtitle = "Start"
          }
        }
      }

      it.updateTile()
    }
  }

  enum class State {
    STATE_OFF,
    STATE_ON,
  }
}