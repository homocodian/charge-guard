package com.homocodian.chargeguard.domain.repository

interface PreferenceDataStoreRepository {
  abstract suspend fun putInt(key: String, value: Int)
  abstract suspend fun getInt(key: String): Int?

  abstract suspend fun putBoolean(key: String, value: Boolean)
  abstract suspend fun getBoolean(key: String): Boolean?
}