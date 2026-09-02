package com.goat.infrastructure.scheduler

import com.goat.domain.model.Task
import com.goat.domain.model.TaskStatus
import com.goat.infrastructure.Constants
import com.goat.infrastructure.persistence.repository.InstanceRepository
import com.goat.infrastructure.persistence.repository.TaskRepository

internal abstract class TaskStepScheduler(
    protected val taskRepository: TaskRepository,
    protected val instanceRepository: InstanceRepository,
) {

    fun getTasksWithStatus(vararg status: TaskStatus): List<Task> {
        val instances = instanceRepository.findAllInstances()
        return taskRepository.findAllTasksByStatus(*status)
            .filter {
                it.processingInstance == null
                        && it.id.hashCode() % instances.size == instances.indexOfFirst { instance ->
                    instance.instanceId == Constants.INSTANCE_ID
                }
            }
    }
}