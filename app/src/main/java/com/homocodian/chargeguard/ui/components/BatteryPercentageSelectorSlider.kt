package com.homocodian.chargeguard.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homocodian.chargeguard.ui.viewmodel.HomeViewModel
import kotlin.math.roundToInt

@Composable
fun BatteryPercentageSelectorSlider() {
  val homeViewModel = hiltViewModel<HomeViewModel>()

  val batteryPercentageToMonitor =
    homeViewModel.batteryPercentageToMonitor.collectAsStateWithLifecycle()

  Column {
    Text(text = "${batteryPercentageToMonitor.value}%")
    Slider(
      value = batteryPercentageToMonitor.value.toFloat(), onValueChange = {
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