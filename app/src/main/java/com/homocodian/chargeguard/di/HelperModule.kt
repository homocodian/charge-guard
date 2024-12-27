package com.homocodian.chargeguard.di

import android.app.Application
import com.homocodian.chargeguard.util.helper.PermissionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HelperModule {

  @Provides
  @Singleton
  fun providesPermissionHelper(appContext: Application): PermissionHelper {
    return PermissionHelper(appContext)
  }
}