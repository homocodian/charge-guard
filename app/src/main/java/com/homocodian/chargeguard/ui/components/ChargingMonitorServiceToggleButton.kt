package com.homocodian.chargeguard.ui.components

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.util.findActivity
import com.homocodian.chargeguard.util.openAppSettings

@Composable
fun ChargingMonitorServiceToggleButton(
  isServiceRunning: Boolean,
  hasNotificationPermission: Boolean,
  shouldShowNotificationStatusAlertDialog: Boolean,
  setShouldShowNotificationStatsAlertDialog: (Boolean) -> Unit,
  setHasNotificationPermission: (Boolean) -> Unit,
  shouldShowRational: () -> Unit,
  notificationPermissionAskCount: Int,
  increaseNotificationPermissionAskCount: () -> Unit,
  onStart: () -> Unit,
  onStop: () -> Unit
) {
  val context = LocalContext.current

  val notificationPermissionResultLauncher =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
      onResult = { isGranted ->
        Log.d(TAG, "isGranted : $isGranted")
        setHasNotificationPermission(isGranted)
        if (isGranted) {
          onStart()
        }
        // NOTE: THIS ORDER IS NECESSARY
        // ELSE IT WILL SHOW RATIONAL DIALOG JUST AFTER
        // DECLINING SECOND PERMISSION REQUEST
        // WHICH IS NOT A GOOD UX
        // if notification permission rejected 2 times
        // show alert dialog to manually give permission on third request
        shouldShowRational()
        increaseNotificationPermissionAskCount()
      })

  Button(
    onClick = {
      when {
        isServiceRunning -> {
          onStop()
        }

        hasNotificationPermission -> {
          Log.d(TAG, "StartMonitoringBatteryChargingLevelButton: onclick")
          onStart()
        }

        shouldShowRequestPermissionRationale(
          context.findActivity(), Manifest.permission.POST_NOTIFICATIONS
        ) -> {
          setShouldShowNotificationStatsAlertDialog(true)
        }

        else -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }
      }
    }) {
    Text( if (isServiceRunning) "Stop" else "Start")
  }

  if (shouldShowNotificationStatusAlertDialog) {
    PermissionDialog(permissionTextProvider = NotificationPermissionTextProvider(),
      isPermanentlyDeclined = notificationPermissionAskCount >= 2 && !hasNotificationPermission,
      onDismiss = {
        setShouldShowNotificationStatsAlertDialog(false)
      },
      onOkClick = {
        setShouldShowNotificationStatsAlertDialog(false)
        if (notificationPermissionAskCount >= 2) {
          context.openAppSettings()
        } else {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }
      })
  }
}