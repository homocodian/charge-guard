package com.homocodian.chargeguard.domain.repository

interface ServiceRepository {
  abstract fun start()
  abstract fun stop()
}