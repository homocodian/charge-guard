package com.homocodian.chargeguard.infra.repository

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.homocodian.chargeguard.dataStore
import com.homocodian.chargeguard.domain.repository.PreferenceDataStoreRepository
import kotlinx.coroutines.flow.first

class PreferenceDataStoreRepository(
  private val appContext: Application
) : PreferenceDataStoreRepository {

  override suspend fun putInt(key: String, value: Int) {
    val key = intPreferencesKey(key)

    appContext.dataStore.edit { preference ->
      preference[key] = value
    }
  }

  override suspend fun getInt(key: String): Int? {
    val key = intPreferencesKey(key)

    val preference = appContext.dataStore.data.first();

    return preference[key]
  }
}