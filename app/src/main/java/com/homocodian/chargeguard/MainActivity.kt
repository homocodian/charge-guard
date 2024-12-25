package com.homocodian.chargeguard

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import com.homocodian.chargeguard.ui.components.BatteryPercentageSelectorSlider
import com.homocodian.chargeguard.ui.components.StartMonitoringBatteryChargingLevelButton
import com.homocodian.chargeguard.ui.components.StopMonitoringBatteryChargingLevel
import com.homocodian.chargeguard.ui.theme.ChargeGuardTheme
import com.homocodian.chargeguard.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "battery_guard")

const val TAG = "Logs"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()

    setContent {
      ChargeGuardTheme {
        val context = LocalContext.current

        val homeViewModel = hiltViewModel<HomeViewModel>()

        LaunchedEffect(true) {
          homeViewModel.event.collect { event ->
            when (event) {
              is HomeViewModel.ScreenEvents.ShowToast -> {
                Toast.makeText(context, event.message, event.duration).show()
              }
            }
          }
        }

        Surface {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            BatteryPercentageSelectorSlider(homeViewModel = homeViewModel)

            StartMonitoringBatteryChargingLevelButton(homeViewModel = homeViewModel) {
              homeViewModel.startChargingDetector()
            }

            StopMonitoringBatteryChargingLevel(homeViewModel = homeViewModel) {
              homeViewModel.stopChargingServiceDetector()
            }
          }
        }
      }
    }
  }
}
