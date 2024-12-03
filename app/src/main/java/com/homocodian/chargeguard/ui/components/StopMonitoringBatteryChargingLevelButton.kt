package com.homocodian.chargeguard.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun StopMonitoringBatteryChargingLevel(onClick: () -> Unit) {
  Button(
    onClick = onClick
  ) {
    Text("Stop")
  }
}