package com.homocodian.chargeguard.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
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
import com.homocodian.chargeguard.store.ChargingStatusServiceState
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

  val shouldShowNotificationStatusAlertDialog by
  homeViewModel.shouldShowNotificationStatusAlertDialog.collectAsStateWithLifecycle()

  val notificationPermissionAskCount by
  homeViewModel.notificationPermissionAskCount.collectAsStateWithLifecycle()

  val isServiceRunning by ChargingStatusServiceState.state.collectAsStateWithLifecycle()

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
        // if notification permission rejected 2 times
        // show alert dialog to manually give permission on third request
        homeViewModel.shouldShowRational()
        homeViewModel.increaseNotificationPermissionAskCount()
      })

  Button(
    enabled = !(isServiceRunning),
    onClick = {
      when {
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
      isPermanentlyDeclined = notificationPermissionAskCount >= 2 && !hasNotificationPermission,
      onDismiss = {
        homeViewModel.setShouldShowNotificationStatsAlertDialog(false)
      },
      onOkClick = {
        homeViewModel.setShouldShowNotificationStatsAlertDialog(false)
        if (notificationPermissionAskCount >= 2) {
          context.findActivity().openAppSettings()
        } else {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }
      })
  }
}