package com.homocodian.chargeguard.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.ui.viewmodel.HomeViewModel
import com.homocodian.chargeguard.util.findActivity
import com.homocodian.chargeguard.util.openAppSettings

@SuppressLint("BatteryLife")
@Composable
fun StartMonitoringBatteryChargingLevelButton(
  homeViewModel: HomeViewModel,
  onClick: () -> Unit
) {
  val context = LocalContext.current

  val hasNotificationPermission by
    homeViewModel.hasNotificationPermission.collectAsStateWithLifecycle()

  val isIgnoringBatteryOptimizations by
    homeViewModel.isIgnoringBatteryOptimizations.collectAsStateWithLifecycle()

  val shouldShowNotificationStatusAlertDialog by
    homeViewModel.shouldShowNotificationStatusAlertDialog.collectAsStateWithLifecycle()

  val notificationPermissionAskCount by
    homeViewModel.notificationPermissionAskCount.collectAsStateWithLifecycle()

  val isServiceRunning by homeViewModel.isDetectorServiceRunning.collectAsStateWithLifecycle()

  val notificationPermissionResultLauncher =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
      onResult = { isGranted ->
        Log.d(TAG, "isGranted : $isGranted")
        homeViewModel.setHasNotificationPermission(state = isGranted)
        if (isGranted) {
          onClick()
        }
        // NOTE: THIS ORDER IS NECESSARY
        // ELSE IT WILL SHOW RATIONAL DIALOG JUST AFTER
        // DECLINING SECOND PERMISSION REQUEST
        // WHICH IS NOT A GOOD UX
        homeViewModel.shouldShowRational()
        homeViewModel.increaseNotificationPermissionAskCount()
      })

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val isIgnoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(context.packageName)

    if (isIgnoringBatteryOptimizations) {
      Log.d(TAG, "Battery optimization ignored.")
      homeViewModel.setIsIgnoringBatteryOptimizations(true)
    } else {
      Log.d(TAG, "Battery optimization not ignored.")
      homeViewModel.setIsIgnoringBatteryOptimizations(false)
    }
  }

  Button(
    enabled = !isServiceRunning,
    onClick = {
    when {
      !(isIgnoringBatteryOptimizations) -> {
        Toast.makeText(
          context,
          "Ignore this application from battery optimization",
          Toast.LENGTH_LONG
        ).show()
        val intent = Intent().apply {
          action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
          data = Uri.parse("package:" + context.packageName)
        }
        launcher.launch(intent)
      }

      hasNotificationPermission -> {
        Log.d(TAG, "StartMonitoringBatteryChargingLevelButton: onclick")
        onClick()
      }

      shouldShowRequestPermissionRationale(
        context.findActivity(), Manifest.permission.POST_NOTIFICATIONS
      ) -> {
        homeViewModel.setShouldShowNotificationStatsAlertDialog(true)
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

  if (shouldShowNotificationStatusAlertDialog) {
    PermissionDialog(permissionTextProvider = NotificationPermissionTextProvider(),
      isPermanentlyDeclined = notificationPermissionAskCount >=2 && !hasNotificationPermission,
      onDismiss = {
        homeViewModel.setShouldShowNotificationStatsAlertDialog(false)
      },
      onOkClick = {
        homeViewModel.setShouldShowNotificationStatsAlertDialog(false)
        if (notificationPermissionAskCount >= 2) {
          context.findActivity().openAppSettings()
        }else{
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }
      })
  }
}