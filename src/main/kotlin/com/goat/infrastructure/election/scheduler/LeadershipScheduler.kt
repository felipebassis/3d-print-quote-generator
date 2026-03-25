package com.goat.infrastructure.election.scheduler

import com.goat.infrastructure.Constants.LAST_ALLOWED_HEARTBEAT_DURATION
import com.goat.infrastructure.election.jpa.entity.InstanceStatus
import com.goat.infrastructure.election.jpa.repository.InstanceRepository
import com.goat.infrastructure.election.jpa.repository.LeaderRepository
import com.goat.infrastructure.extensions.Loggable
import io.quarkus.scheduler.Scheduled
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@Singleton
internal class LeadershipScheduler(
    private val leaderRepository: LeaderRepository,
    private val instanceRepository: InstanceRepository,
) : Loggable {

    @Scheduled(every = "15s")
    @Transactional(Transactional.TxType.REQUIRED)
    fun checkRunningInstances() =
        if (leaderRepository.isCurrentLeader()) {
            logger.info("Checking running instances...")
            val notDeadInstances = instanceRepository.findAllNotDead()
            val deadInstances = notDeadInstances.filter {
                it.status == InstanceStatus.SHUTTING_DOWN ||
                        it.lastHeartbeat.isBefore(
                            LocalDateTime.now()
                                .minus(LAST_ALLOWED_HEARTBEAT_DURATION)
                        )
            }
            if (deadInstances.isNotEmpty()) {
                logger.warn(
                    "Instances {} are not running anymore. Redistributing pending tasks to running instances.",
                    deadInstances.joinToString(", ") { it.instanceId }
                )
                deadInstances.forEach {
                    it.status = InstanceStatus.DEAD
                    instanceRepository.save(it)
                }
                TODO("Implementar rebalanceamento de tarefas")
            } else {
                logger.info("All instances are running.")
            }
        } else {
            logger.debug("I'm not the leader, this is not my job.")
        }
}