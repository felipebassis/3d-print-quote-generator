package com.goat.infrastructure.scheduler

import com.goat.infrastructure.Constants.LAST_ALLOWED_HEARTBEAT_DURATION
import com.goat.infrastructure.persistence.enums.InstanceStatus
import com.goat.infrastructure.persistence.repository.InstanceRepository
import com.goat.infrastructure.persistence.repository.LeaderRepository
import com.goat.infrastructure.extensions.Loggable
import com.goat.infrastructure.persistence.repository.TaskRepository
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@ApplicationScoped
internal class LeadershipScheduler(
    private val leaderRepository: LeaderRepository,
    private val instanceRepository: InstanceRepository,
    private val taskRepository: TaskRepository,
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
                logger.debug(
                    "Instances {} are not running anymore. Redistributing pending tasks to running instances.",
                    deadInstances.joinToString(", ") { it.instanceId }
                )
                deadInstances.forEach {
                    it.status = InstanceStatus.DEAD
                    instanceRepository.save(it)
                    taskRepository.redistributeForInstance(it.instanceId)
                }
                logger.debug("Tasks rebalanced.")
            } else {
                logger.debug("All instances are running.")
            }
        } else {
            logger.debug("I'm not the leader, this is not my job.")
        }
}