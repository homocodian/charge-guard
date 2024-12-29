package com.homocodian.chargeguard

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.homocodian.chargeguard.ui.HomeScreen
import com.homocodian.chargeguard.ui.theme.ChargeGuardTheme
import com.homocodian.chargeguard.util.helper.TileHelper
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
        HomeScreen()
      }
    }
  }

  override fun onStart() {
    super.onStart()
    TileHelper.requestTileListeningState(this)
  }
}
