package com.goat.infrastructure.election.scheduler

import com.goat.infrastructure.election.jpa.repository.LeaderRepository
import com.goat.infrastructure.extensions.Loggable
import io.quarkus.scheduler.Scheduled
import jakarta.inject.Singleton

@Singleton
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