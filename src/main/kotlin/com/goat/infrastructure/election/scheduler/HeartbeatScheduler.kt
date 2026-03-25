package com.goat.infrastructure.election.scheduler

import com.goat.infrastructure.election.jpa.repository.InstanceRepository
import io.quarkus.scheduler.Scheduled
import jakarta.inject.Singleton

@Singleton
internal class HeartbeatScheduler(
    private val instanceRepository: InstanceRepository,
) {

    @Scheduled(every = "5s")
    fun heartBeat() {
        instanceRepository.updateHeartBeat()
    }
}