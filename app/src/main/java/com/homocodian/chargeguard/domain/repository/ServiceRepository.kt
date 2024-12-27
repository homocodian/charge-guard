package com.homocodian.chargeguard.domain.repository

interface ServiceRepository {
  abstract fun requestStart()
  abstract fun requestStop()
  abstract fun start()
  abstract fun stop()
}