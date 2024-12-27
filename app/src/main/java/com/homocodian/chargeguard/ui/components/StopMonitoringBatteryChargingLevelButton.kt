package com.homocodian.chargeguard.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homocodian.chargeguard.store.ChargingStatusServiceState

@Composable
fun StopMonitoringBatteryChargingLevel(
  onClick: () -> Unit
) {
  val isServiceRunning by ChargingStatusServiceState.state.collectAsStateWithLifecycle()

  Button(
    enabled = isServiceRunning,
    onClick = onClick
  ) {
    Text("Stop")
  }
}