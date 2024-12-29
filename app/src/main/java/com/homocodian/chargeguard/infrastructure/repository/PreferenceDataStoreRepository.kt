package com.homocodian.chargeguard.infrastructure.repository

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.homocodian.chargeguard.dataStore
import kotlinx.coroutines.flow.first

class PreferenceDataStoreRepository(
  private val applicationContext: Application
) {
  suspend fun putInt(key: String, value: Int) {
    val key = intPreferencesKey(key)

    applicationContext.dataStore.edit { preference ->
      preference[key] = value
    }
  }

  suspend fun getInt(key: String): Int? {
    val key = intPreferencesKey(key)

    val preference = applicationContext.dataStore.data.first()

    return preference[key]
  }
}