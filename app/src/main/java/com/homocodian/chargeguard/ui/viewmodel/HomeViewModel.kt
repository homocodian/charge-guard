package com.homocodian.chargeguard.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.constant.BatteryLevel
import com.homocodian.chargeguard.infrastructure.repository.ChargingLevelServiceRepository
import com.homocodian.chargeguard.infrastructure.repository.PowerConnectionServiceRepository
import com.homocodian.chargeguard.infrastructure.repository.PreferenceDataStoreRepository
import com.homocodian.chargeguard.ui.model.home.HomeUiState
import com.homocodian.chargeguard.ui.model.home.NotificationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val applicationContext: Application,
  private val powerConnectionServiceRepository: PowerConnectionServiceRepository,
  private val chargingLevelServiceRepository: ChargingLevelServiceRepository,
  private val preferenceDataStoreRepository: PreferenceDataStoreRepository
) : ViewModel() {

  private var saveBatteryPercentageJob: Job? = null
  private var increaseNotificationPermissionCountJob: Job? = null

  private val lastBatteryPercentageSaved = MutableStateFlow<Int?>(null)

  private var _uiState = MutableStateFlow(HomeUiState())
  val uiState = _uiState.asStateFlow()

  val isServiceRunning = powerConnectionServiceRepository.isServiceRunningFlow

  private var _notificationState = MutableStateFlow(NotificationState())
  val notificationState = _notificationState.asStateFlow()

  fun setShouldShowNotificationStatsAlertDialog(value: Boolean) {
    _uiState.value = _uiState.value.copy(shouldShowNotificationStatusAlertDialog = value)
  }

  fun setBatteryPercentageToMonitor(value: Int) {
    _uiState.value = _uiState.value.copy(batteryPercentageToMonitor = value)
  }

  fun setNotificationPermissionAskCount(value: Int) {
    _notificationState.value = _notificationState.value.copy(
      notificationPermissionAskCount = value
    )
  }

  fun setHasNotificationPermission(state: Boolean) {
    _notificationState.value = _notificationState.value.copy(
      hasNotificationPermission = state
    )
  }

  fun saveBatteryPercentageToMonitor() {
    Log.d(TAG, "saveBatteryPercentageToMonitor: ${uiState.value.batteryPercentageToMonitor}")

    if (lastBatteryPercentageSaved.value == uiState.value.batteryPercentageToMonitor) {
      Log.d(TAG, "saveBatteryPercentageToMonitor: Same value returning")
      return
    }

    saveBatteryPercentageJob?.cancel()

    saveBatteryPercentageJob = viewModelScope.launch {
      try {
        withContext(Dispatchers.IO) {
          preferenceDataStoreRepository.putInt(
            key = BatteryLevel.DataStore.BATTERY_LEVEL_TO_MONITOR_KEY,
            value = uiState.value.batteryPercentageToMonitor
          )
        }

        lastBatteryPercentageSaved.value = uiState.value.batteryPercentageToMonitor

        if (powerConnectionServiceRepository.isServiceRunning) {
          Toast.makeText(
            applicationContext,
            "You will be notified when the battery " +
              "reaches ${uiState.value.batteryPercentageToMonitor}%",
            Toast.LENGTH_SHORT
          ).show()
        }

        if (powerConnectionServiceRepository.isServiceRunning) {
          chargingLevelServiceRepository.stop()
          chargingLevelServiceRepository.start()
        }
      } catch (e: Exception) {
        if (e is CancellationException) throw e
        Toast.makeText(applicationContext, "Failed to save battery level", Toast.LENGTH_SHORT)
          .show()
      }
    }
  }

  fun increaseNotificationPermissionAskCount() {
    if (notificationState.value.notificationPermissionAskCount >= 2) return

    setNotificationPermissionAskCount(notificationState.value.notificationPermissionAskCount + 1)

    increaseNotificationPermissionCountJob?.cancel()

    increaseNotificationPermissionCountJob = viewModelScope.launch(Dispatchers.IO) {
      preferenceDataStoreRepository.putInt(
        key = BatteryLevel.DataStore.ASKED_NOTIFICATION_COUNT,
        value = notificationState.value.notificationPermissionAskCount
      )
    }
  }

  fun shouldShowRational() {
    if (!notificationState.value.hasNotificationPermission
      && notificationState.value.notificationPermissionAskCount >= 2
      && !uiState.value.shouldShowNotificationStatusAlertDialog
    ) {
      setShouldShowNotificationStatsAlertDialog(true)
    }
  }

  fun startChargingDetector() {
    powerConnectionServiceRepository.start()
    Toast.makeText(applicationContext, "Charging detection is now active", Toast.LENGTH_SHORT)
      .show()
  }

  fun stopChargingServiceDetector() {
    powerConnectionServiceRepository.stop()
    Toast.makeText(
      applicationContext, "Battery charging detection is now inactive", Toast.LENGTH_SHORT
    ).show()
  }

  init {
    Log.d(TAG, "init HomeViewModel")

    viewModelScope.launch(Dispatchers.IO) {
      val data =
        preferenceDataStoreRepository.getInt(BatteryLevel.DataStore.BATTERY_LEVEL_TO_MONITOR_KEY)
          ?: BatteryLevel.DEFAULT_BATTERY_LEVEL_TO_MONITOR

      setBatteryPercentageToMonitor(data)
      Log.d(TAG, "Init battery level: $data")
    }

    viewModelScope.launch {
      val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
          applicationContext, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
      } else {
        true
      }

      setHasNotificationPermission(hasNotificationPermission)
      Log.d(TAG, "init notification permission: $hasNotificationPermission")
    }

    viewModelScope.launch(Dispatchers.IO) {
      val count =
        preferenceDataStoreRepository.getInt(key = BatteryLevel.DataStore.ASKED_NOTIFICATION_COUNT)

      if (count != null) {
        setNotificationPermissionAskCount(count)
      }
    }
  }
}