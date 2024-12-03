package com.homocodian.chargeguard.di

import android.app.Application
import com.homocodian.chargeguard.infra.repository.ChargingDetectorServiceRepository
import com.homocodian.chargeguard.infra.repository.ChargingLevelDetectorServiceRepository
import com.homocodian.chargeguard.infra.repository.PowerConnectionServiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceRepositoryModule {

  @Provides
  @Singleton
  fun providesChargingDetectorServiceRepository(appContext: Application): ChargingDetectorServiceRepository {
    return ChargingDetectorServiceRepository(appContext)
  }

  @Provides
  @Singleton
  fun providesChargingLevelDetectorServiceRepository(appContext: Application): ChargingLevelDetectorServiceRepository {
    return ChargingLevelDetectorServiceRepository(appContext)
  }

  @Provides
  @Singleton
  fun providesPowerConnectionServiceRepository(
    appContext: Application,
    chargingDetectorServiceRepository: ChargingDetectorServiceRepository
  ): PowerConnectionServiceRepository {
    return PowerConnectionServiceRepository(appContext, chargingDetectorServiceRepository)
  }

}