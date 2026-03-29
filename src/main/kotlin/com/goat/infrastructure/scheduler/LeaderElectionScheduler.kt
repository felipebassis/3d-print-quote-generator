package com.goat.infrastructure.scheduler

import com.goat.infrastructure.persistence.repository.LeaderRepository
import com.goat.infrastructure.extensions.Loggable
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
internal class LeaderElectionScheduler(
    private val leaderRepository: LeaderRepository,
) : Loggable {

    @Scheduled(every = "5s")
    fun beginElection() {
        val isCurrentLeader = leaderRepository.updateToCurrentLeader()
        if (isCurrentLeader) {
            logger.debug("This instance is the leader.")
        } else {
            logger.debug("Election lost. This instance is not a leader.")
        }
    }
}