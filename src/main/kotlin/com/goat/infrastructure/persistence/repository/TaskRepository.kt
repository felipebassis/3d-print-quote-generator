package com.goat.infrastructure.persistence.repository

import com.goat.domain.model.Task
import com.goat.domain.model.TaskStatus

interface TaskRepository {

    fun save(task: Task)
    fun redistributeForInstance(instanceId: String)
    fun findAllTasksByStatus(vararg status: TaskStatus): List<Task>
}