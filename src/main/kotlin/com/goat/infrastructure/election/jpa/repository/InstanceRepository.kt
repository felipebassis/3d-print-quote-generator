package com.goat.infrastructure.election.jpa.repository

import com.goat.infrastructure.election.jpa.entity.InstanceEntity

interface InstanceRepository {

    fun save(instance: InstanceEntity)
    fun updateHeartBeat()
    fun markAsShuttingDown()
    fun findAllNotDead(): List<InstanceEntity>
}