package com.homocodian.chargeguard.di

import android.app.Application
import com.homocodian.chargeguard.infrastructure.repository.PreferenceDataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceDataStoreModule {

  @Provides
  @Singleton
  fun providesPreferenceDataStoreRepository(appContext: Application): PreferenceDataStoreRepository {
    return PreferenceDataStoreRepository(appContext)
  }

}