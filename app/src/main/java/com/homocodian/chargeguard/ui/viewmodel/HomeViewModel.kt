package com.homocodian.chargeguard.ui.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homocodian.chargeguard.TAG
import com.homocodian.chargeguard.constant.BatteryLevel
import com.homocodian.chargeguard.infra.repository.PowerConnectionServiceRepository
import com.homocodian.chargeguard.infra.repository.PreferenceDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val appContext: Application,
  private val powerConnectionServiceRepository: PowerConnectionServiceRepository,
  private val preferenceDataStoreRepository: PreferenceDataStoreRepository
) : ViewModel() {

  private var job: Job? = null

  val batteryPercentageToMonitor = savedStateHandle.getStateFlow(
    key = "batteryPercentageToMonitor",
    initialValue = BatteryLevel.DEFAULT_BATTERY_LEVEL_TO_MONITOR
  )

  private val lastBatteryPercentageSaved = MutableStateFlow<Int?>(null)

  init {
    Log.d(TAG, "init HomeViewModel")
    viewModelScope.launch(Dispatchers.IO) {
      val data =
        preferenceDataStoreRepository.getInt(BatteryLevel.DataStore.BATTERY_LEVEL_TO_MONITOR_KEY)
          ?: BatteryLevel.DEFAULT_BATTERY_LEVEL_TO_MONITOR

      savedStateHandle["batteryPercentageToMonitor"] = data
    }
  }

  val isIgnoringBatteryOptimizations = savedStateHandle.getStateFlow(
    key = "isIgnoringBatteryOptimizations",
    initialValue = isIgnoringBatteryOptimizations()
  )

  val hasNotificationPermission = savedStateHandle.getStateFlow(
    key = "hasNotificationPermission",
    initialValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        appContext, Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
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

    job?.cancel()

    job = viewModelScope.launch(Dispatchers.IO) {
      preferenceDataStoreRepository.putInt(
        key = BatteryLevel.DataStore.BATTERY_LEVEL_TO_MONITOR_KEY,
        value = batteryPercentageToMonitor.value
      )
    }

    lastBatteryPercentageSaved.value = batteryPercentageToMonitor.value
  }

  fun setHasNotificationPermission(state: Boolean) {
    savedStateHandle["hasNotificationPermission"] = state
  }

  private fun setIsIgnoringBatteryOptimizations(state: Boolean) {
    savedStateHandle["isIgnoringBatteryOptimizations"] = state
  }

  private fun isIgnoringBatteryOptimizations(): Boolean {
    return (appContext.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
      appContext.packageName
    )
  }

  private fun getIsIgnoringBatteryOptimizationsAndSave(): Boolean {
    val isIgnoringBatteryOptimizations = isIgnoringBatteryOptimizations()

    if (isIgnoringBatteryOptimizations != this.isIgnoringBatteryOptimizations.value) {
      setIsIgnoringBatteryOptimizations(isIgnoringBatteryOptimizations)
    }

    return isIgnoringBatteryOptimizations
  }

  @SuppressLint("BatteryLife")
  fun openBatteryOptimizationSettings(context: Context) {
    if (!getIsIgnoringBatteryOptimizationsAndSave()) {
      val intent = Intent()
      intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
      intent.data = Uri.parse("package:" + context.packageName)
      context.startActivity(intent)
    }
  }

  fun startChargingDetector() {
    powerConnectionServiceRepository.start()
  }

  fun stopChargingServiceDetector() {
    powerConnectionServiceRepository.stop()
  }

//  init {
//    viewModelScope.launch {
//      combine()
//    }
//  }

}