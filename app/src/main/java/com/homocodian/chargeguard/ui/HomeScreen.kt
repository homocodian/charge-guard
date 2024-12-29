package com.homocodian.chargeguard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homocodian.chargeguard.ui.components.BatteryPercentageSelectorSlider
import com.homocodian.chargeguard.ui.components.ChargingMonitorServiceToggleButton
import com.homocodian.chargeguard.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
  homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
  val isServiceRunning by homeViewModel.isServiceRunning.collectAsStateWithLifecycle()

  val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
  val notificationState by homeViewModel.notificationState.collectAsStateWithLifecycle()

  Surface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 10.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      BatteryPercentageSelectorSlider(
        value = uiState.batteryPercentageToMonitor,
        onValueChange = homeViewModel::setBatteryPercentageToMonitor,
        onValueChangeFinished = homeViewModel::saveBatteryPercentageToMonitor
      )

      ChargingMonitorServiceToggleButton(
        isServiceRunning = isServiceRunning,
        onStart = homeViewModel::startChargingDetector,
        onStop = homeViewModel::stopChargingServiceDetector,
        shouldShowRational = homeViewModel::shouldShowRational,
        hasNotificationPermission = notificationState.hasNotificationPermission,
        setHasNotificationPermission = homeViewModel::setHasNotificationPermission,
        increaseNotificationPermissionAskCount = homeViewModel::increaseNotificationPermissionAskCount,
        setShouldShowNotificationStatsAlertDialog = homeViewModel::setShouldShowNotificationStatsAlertDialog,
        notificationPermissionAskCount = notificationState.notificationPermissionAskCount,
        shouldShowNotificationStatusAlertDialog = uiState.shouldShowNotificationStatusAlertDialog
      )
    }
  }
}