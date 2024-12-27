package com.homocodian.chargeguard.di

import android.app.Application
import com.homocodian.chargeguard.infra.repository.ChargingStatusServiceRepository
import com.homocodian.chargeguard.infra.repository.ChargingLevelServiceRepository
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
  fun providesChargingDetectorServiceRepository(appContext: Application): ChargingStatusServiceRepository {
    return ChargingStatusServiceRepository(appContext)
  }

  @Provides
  @Singleton
  fun providesChargingLevelDetectorServiceRepository(appContext: Application): ChargingLevelServiceRepository {
    return ChargingLevelServiceRepository(appContext)
  }

  @Provides
  @Singleton
  fun providesPowerConnectionServiceRepository(
    appContext: Application,
    chargingStatusServiceRepository: ChargingStatusServiceRepository
  ): PowerConnectionServiceRepository {
    return PowerConnectionServiceRepository(appContext, chargingStatusServiceRepository)
  }

}