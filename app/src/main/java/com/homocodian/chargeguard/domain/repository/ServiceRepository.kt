package com.homocodian.chargeguard.domain.repository

interface ServiceRepository {
  fun start()
  fun stop()
}