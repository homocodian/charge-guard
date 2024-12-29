package com.homocodian.chargeguard.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlin.math.roundToInt

@Composable
fun BatteryPercentageSelectorSlider(
  value: Int,
  onValueChange: (Int) -> Unit,
  onValueChangeFinished: () -> Unit
) {
  Column {
    Text(text = "${value}%")
    Slider(
      value = value.toFloat(),
      onValueChange = {
        if (it >= 50f) {
          onValueChange(it.roundToInt())
        }
      },
      onValueChangeFinished = onValueChangeFinished,
      valueRange = 0f..100f
    )
  }
}