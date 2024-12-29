package com.homocodian.chargeguard.di

import android.app.Application
import com.homocodian.chargeguard.infrastructure.repository.ChargingLevelServiceRepository
import com.homocodian.chargeguard.infrastructure.repository.PowerConnectionServiceRepository
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
  fun providesChargingLevelServiceRepository(appContext: Application): ChargingLevelServiceRepository {
    return ChargingLevelServiceRepository(appContext)
  }

  @Provides
  @Singleton
  fun providesPowerConnectionServiceRepository(
    appContext: Application
  ): PowerConnectionServiceRepository {
    return PowerConnectionServiceRepository(appContext)
  }

}