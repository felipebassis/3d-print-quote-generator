package com.goat.infrastructure.scheduler

import com.goat.infrastructure.persistence.repository.InstanceRepository
import com.goat.infrastructure.extensions.Loggable
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
internal class HeartbeatScheduler(
    private val instanceRepository: InstanceRepository,
): Loggable {

    @Scheduled(every = "5s")
    fun heartBeat() {
        logger.debug("Sending heartbeat...")
        instanceRepository.updateHeartBeat()
    }
}