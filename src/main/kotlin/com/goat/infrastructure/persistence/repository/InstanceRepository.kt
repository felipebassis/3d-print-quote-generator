package com.goat.infrastructure.persistence.repository

import com.goat.infrastructure.persistence.entity.InstanceEntity

interface InstanceRepository {

    fun save(instance: InstanceEntity)
    fun updateHeartBeat()
    fun markAsShuttingDown()
    fun findAllNotDead(): List<InstanceEntity>
    fun findAllInstances(): List<InstanceEntity>
}