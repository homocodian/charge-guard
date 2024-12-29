package com.homocodian.chargeguard.infrastructure.repository

import android.app.Application
import android.content.Intent
import com.homocodian.chargeguard.domain.repository.ServiceRepository
import com.homocodian.chargeguard.service.ChargingLevelService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChargingLevelServiceRepository @Inject constructor(
  @ApplicationContext private val applicationContext: Application
) : ServiceRepository {

  override fun start() {
    Intent(applicationContext, ChargingLevelService::class.java).also {
      applicationContext.startService(it)
    }
  }

  override fun stop() {
    Intent(applicationContext, ChargingLevelService::class.java).also {
      applicationContext.stopService(it)
    }
  }
}