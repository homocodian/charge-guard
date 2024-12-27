package com.homocodian.chargeguard.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ChargingStatusServiceState {
  private val _state = MutableStateFlow(false)
  val state = _state.asStateFlow()

  fun setState(isRunning: Boolean) {
    _state.value = isRunning
  }
}