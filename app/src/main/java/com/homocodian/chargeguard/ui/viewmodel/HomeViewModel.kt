package com.homocodian.chargeguard.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.constant.BatteryLevel
import com.homocodian.chargeguard.infra.repository.ChargingLevelDetectorServiceRepository
import com.homocodian.chargeguard.infra.repository.PowerConnectionServiceRepository
import com.homocodian.chargeguard.infra.repository.PreferenceDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val appContext: Application,
  private val powerConnectionServiceRepository: PowerConnectionServiceRepository,
  private val chargingLevelDetectorServiceRepository: ChargingLevelDetectorServiceRepository,
  private val preferenceDataStoreRepository: PreferenceDataStoreRepository
) : ViewModel() {

  private var saveBatteryPercentageJob: Job? = null
  private var increaseNotificationPermissionCountJob: Job? = null

  private val _event = MutableSharedFlow<ScreenEvents>()
  val event = _event.asSharedFlow()

  val isDetectorServiceRunning = savedStateHandle.getStateFlow(
    key = "isDetectorServiceRunning", initialValue = true
  )

  val batteryPercentageToMonitor = savedStateHandle.getStateFlow(
    key = "batteryPercentageToMonitor", initialValue = BatteryLevel.DEFAULT_BATTERY_LEVEL_TO_MONITOR
  )

  private var _notificationPermissionAskCount = MutableStateFlow(0)
  val notificationPermissionAskCount = _notificationPermissionAskCount.asStateFlow()

  private val _shouldShowNotificationStatusAlertDialog = MutableStateFlow(false)
  val shouldShowNotificationStatusAlertDialog =
    _shouldShowNotificationStatusAlertDialog.asStateFlow()

  fun setShouldShowNotificationStatsAlertDialog(value: Boolean) {
    _shouldShowNotificationStatusAlertDialog.value = value
  }

  private val lastBatteryPercentageSaved = MutableStateFlow<Int?>(null)

  val isIgnoringBatteryOptimizations = savedStateHandle.getStateFlow(
    key = "isIgnoringBatteryOptimizations", initialValue = false
  )

  val hasNotificationPermission = savedStateHandle.getStateFlow(
    key = "hasNotificationPermission", initialValue = false
  )

  fun setBatteryPercentageToMonitor(value: Int) {
    savedStateHandle["batteryPercentageToMonitor"] = value
  }

  fun saveBatteryPercentageToMonitor() {
    Log.d(TAG, "saveBatteryPercentageToMonitor: ${batteryPercentageToMonitor.value}")

    if (lastBatteryPercentageSaved.value == batteryPercentageToMonitor.value) {
      Log.d(TAG, "saveBatteryPercentageToMonitor: Same value returning")
      return
    }

    saveBatteryPercentageJob?.cancel()

    saveBatteryPercentageJob = viewModelScope.launch(Dispatchers.IO) {
      try {
        preferenceDataStoreRepository.putInt(
          key = BatteryLevel.DataStore.BATTERY_LEVEL_TO_MONITOR_KEY,
          value = batteryPercentageToMonitor.value
        )

        withContext(Dispatchers.Main) {
          if (isDetectorServiceRunning.value) {
            _event.emit(ScreenEvents.ShowToast("You will be notified when the battery reaches ${batteryPercentageToMonitor.value}%"))
          }
          chargingLevelDetectorServiceRepository.restartIfRunning()
        }

      } catch (_: Exception) {
        _event.emit(ScreenEvents.ShowToast(message = "Failed to save battery level"))
      }
    }

    lastBatteryPercentageSaved.value = batteryPercentageToMonitor.value
  }

  fun increaseNotificationPermissionAskCount() {

    if (_notificationPermissionAskCount.value >= 2) {
      return
    }

    _notificationPermissionAskCount.value = _notificationPermissionAskCount.value + 1

    increaseNotificationPermissionCountJob?.cancel()

    increaseNotificationPermissionCountJob = viewModelScope.launch(Dispatchers.IO) {
      val count =
        preferenceDataStoreRepository.getInt(key = BatteryLevel.DataStore.ASKED_NOTIFICATION_COUNT)

      if (count != null) {
        if (count <= 2) {
          preferenceDataStoreRepository.putInt(
            key = BatteryLevel.DataStore.ASKED_NOTIFICATION_COUNT, value = count + 1
          )
        }
      } else {
        preferenceDataStoreRepository.putInt(
          key = BatteryLevel.DataStore.ASKED_NOTIFICATION_COUNT, value = 1
        )
      }
    }
  }

  fun shouldShowRational() {
    if (!hasNotificationPermission.value && _notificationPermissionAskCount.value >= 2 && !shouldShowNotificationStatusAlertDialog.value) {
      setShouldShowNotificationStatsAlertDialog(true)
    }
  }

  fun setHasNotificationPermission(state: Boolean) {
    savedStateHandle["hasNotificationPermission"] = state
  }

  fun setIsIgnoringBatteryOptimizations(state: Boolean) {
    savedStateHandle["isIgnoringBatteryOptimizations"] = state
  }

  fun startChargingDetector() {
    savedStateHandle["isDetectorServiceRunning"] = true

    viewModelScope.launch {
      powerConnectionServiceRepository.start()
      _event.emit(ScreenEvents.ShowToast("Charging detection is now active"))
    }
  }

  fun stopChargingServiceDetector() {
    savedStateHandle["isDetectorServiceRunning"] = false

    viewModelScope.launch {
      powerConnectionServiceRepository.stop()
      _event.emit(ScreenEvents.ShowToast("Battery charging detection is now inactive"))
    }
  }

  init {
    Log.d(TAG, "init HomeViewModel")

    viewModelScope.launch(Dispatchers.IO) {
      val data =
        preferenceDataStoreRepository.getInt(BatteryLevel.DataStore.BATTERY_LEVEL_TO_MONITOR_KEY)
          ?: BatteryLevel.DEFAULT_BATTERY_LEVEL_TO_MONITOR

      savedStateHandle["batteryPercentageToMonitor"] = data
      Log.d(TAG, "Init battery level: $data")
    }

    viewModelScope.launch {
      val isServiceRunning = powerConnectionServiceRepository.isPowerConnectionServiceRunning()
      Log.d(TAG, "Init isServiceRunning: $isServiceRunning")
      savedStateHandle["isDetectorServiceRunning"] = isServiceRunning
    }

    viewModelScope.launch {
      val isIgnoring =
        (appContext.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
          appContext.packageName
        )

      savedStateHandle["isIgnoringBatteryOptimizations"] = isIgnoring
      Log.d(TAG, "init isIgnoring battery optimizations: $isIgnoring")
    }

    viewModelScope.launch {
      val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
          appContext, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
      } else {
        true
      }

      savedStateHandle["hasNotificationPermission"] = hasNotificationPermission
      Log.d(TAG, "init notification permission: $hasNotificationPermission")
    }

    viewModelScope.launch(Dispatchers.IO) {
      val count =
        preferenceDataStoreRepository.getInt(key = BatteryLevel.DataStore.ASKED_NOTIFICATION_COUNT)

      if (count != null) {
        _notificationPermissionAskCount.value = count
      }
    }
  }

  sealed class ScreenEvents {
    data class ShowToast(val message: String, val duration: Int = Toast.LENGTH_SHORT) :
      ScreenEvents()
  }

}