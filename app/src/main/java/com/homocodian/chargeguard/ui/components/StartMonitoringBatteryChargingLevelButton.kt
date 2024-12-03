package com.homocodian.chargeguard.ui.components

import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.ui.viewmodel.HomeViewModel
import com.homocodian.chargeguard.util.findActivity

@Composable
fun StartMonitoringBatteryChargingLevelButton(
  onClick: () -> Unit
) {
  val context = LocalContext.current


  val homeViewModel = hiltViewModel<HomeViewModel>()

  val hasNotificationPermission =
    homeViewModel.hasNotificationPermission.collectAsStateWithLifecycle()

  val isIgnoringBatteryOptimizations =
    homeViewModel.isIgnoringBatteryOptimizations.collectAsStateWithLifecycle()

  val notificationPermissionResultLauncher =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
      onResult = { isGranted ->
        Log.d(TAG, "isGranted : $isGranted")
        homeViewModel.setHasNotificationPermission(state = isGranted)
        if (isGranted) {
          onClick()
        }
      })

  Button(onClick = {

    when {
      !(isIgnoringBatteryOptimizations.value) -> {
        Toast.makeText(
          context,
          "Ignore this application from battery optimization",
          Toast.LENGTH_LONG
        ).show()

        homeViewModel.openBatteryOptimizationSettings(context.findActivity())
      }

      hasNotificationPermission.value -> {
        Log.d(TAG, "StartMonitoringBatteryChargingLevelButton: onclick")
        onClick()
      }

      shouldShowRequestPermissionRationale(
        context.findActivity(), Manifest.permission.POST_NOTIFICATIONS
      ) -> {
        Log.d(TAG, "Rationale")
      }

      else -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      }
    }
  }) {
    Text("Start")
  }
}