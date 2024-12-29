package com.homocodian.chargeguard.ui.model.home

import com.homocodian.chargeguard.constant.BatteryLevel

data class HomeUiState(
  val batteryPercentageToMonitor: Int = BatteryLevel.DEFAULT_BATTERY_LEVEL_TO_MONITOR,
  val shouldShowNotificationStatusAlertDialog: Boolean = false
)