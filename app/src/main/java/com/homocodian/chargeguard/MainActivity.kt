package com.homocodian.chargeguard

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import com.homocodian.chargeguard.broadcasts.MessageReceiver
import com.homocodian.chargeguard.ui.components.BatteryPercentageSelectorSlider
import com.homocodian.chargeguard.ui.components.NotificationPermissionTextProvider
import com.homocodian.chargeguard.ui.components.PermissionDialog
import com.homocodian.chargeguard.ui.components.StartMonitoringBatteryChargingLevelButton
import com.homocodian.chargeguard.ui.components.StopMonitoringBatteryChargingLevel
import com.homocodian.chargeguard.ui.theme.ChargeGuardTheme
import com.homocodian.chargeguard.ui.viewmodel.HomeViewModel
import com.homocodian.chargeguard.util.openAppSettings
import dagger.hilt.android.AndroidEntryPoint

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "battery_guard")

const val TAG = "Logs"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private lateinit var messageReceiver: MessageReceiver

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      ChargeGuardTheme {

        val homeViewModel = hiltViewModel<HomeViewModel>()

        var shouldShowNotificationStatusAlertDialog by remember {
          mutableStateOf(false)
        }

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          BatteryPercentageSelectorSlider()

          StartMonitoringBatteryChargingLevelButton {
            homeViewModel.startChargingDetector()
          }

          StopMonitoringBatteryChargingLevel {
            homeViewModel.stopChargingServiceDetector()
          }
        }

        if (shouldShowNotificationStatusAlertDialog) {
          PermissionDialog(permissionTextProvider = NotificationPermissionTextProvider(),
            onDismiss = {
              shouldShowNotificationStatusAlertDialog = false
            },
            onOkClick = {
              shouldShowNotificationStatusAlertDialog = false
              this.openAppSettings()
            })
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()

    messageReceiver = MessageReceiver()
    val filter = IntentFilter(this.packageName + ".MESSAGE")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      registerReceiver(messageReceiver, filter, RECEIVER_EXPORTED)
    } else {
      registerReceiver(messageReceiver, filter)
    }
  }

  override fun onPause() {
    super.onPause()
    unregisterReceiver(messageReceiver)
  }

}
