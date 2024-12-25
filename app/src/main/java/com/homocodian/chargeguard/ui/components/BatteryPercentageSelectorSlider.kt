package com.homocodian.chargeguard.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homocodian.chargeguard.ui.viewmodel.HomeViewModel
import kotlin.math.roundToInt

@Composable
fun BatteryPercentageSelectorSlider(
  homeViewModel: HomeViewModel
) {

  val batteryPercentageToMonitor by
  homeViewModel.batteryPercentageToMonitor.collectAsStateWithLifecycle()

  Column {
    Text(text = "${batteryPercentageToMonitor}%")
    Slider(
      value = batteryPercentageToMonitor.toFloat(), onValueChange = {
        if (it >= 50f) {
          homeViewModel.setBatteryPercentageToMonitor(it.roundToInt())
        }
      },
      onValueChangeFinished = {
        homeViewModel.saveBatteryPercentageToMonitor()
      },
      valueRange = 0f..100f
    )
  }
}