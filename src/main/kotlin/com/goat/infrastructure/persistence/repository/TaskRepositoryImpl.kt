package com.goat.infrastructure.persistence.repository

import com.goat.domain.model.Customer
import com.goat.domain.model.Task
import com.goat.domain.model.TaskStatus
import com.goat.infrastructure.Constants
import com.goat.infrastructure.persistence.entity.CustomerEntity
import com.goat.infrastructure.persistence.entity.TaskEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import java.time.LocalDateTime
import java.util.UUID

@ApplicationScoped
internal class TaskRepositoryImpl(
    private val entityManager: EntityManager,
) : TaskRepository {

    @Transactional(Transactional.TxType.REQUIRED)
    override fun save(task: Task) =
        entityManager.persist(
            entityManager.find(TaskEntity::class.java, task.id.toString())
                ?.also {
                    it.attempts = task.attempts
                    it.status = task.status
                    it.updatedAt = LocalDateTime.now()
                    it.lastError = task.lastError
                    it.processingInstance = task.processingInstance
                } ?: TaskEntity(
                id = task.id.toString(),
                stlDirectory = task.stlDirectory,
                attempts = task.attempts,
                status = task.status,
                createdAt = task.createdAt,
                customer = CustomerEntity(
                    customerName = task.customer.customerName,
                    customerPhone = task.customer.customerPhone,
                    customerEmail = task.customer.customerEmail,
                    customerZipCode = task.customer.customerZipCode,
                )
            )
        )

    @Transactional(Transactional.TxType.REQUIRED)
    override fun redistributeForInstance(instanceId: String) {
        entityManager.createQuery(
            """
            UPDATE TaskEntity t set t.processingInstance = NULL where t.processingInstance = :instanceId
        """.trimIndent()
        ).setParameter("instanceId", instanceId)
            .executeUpdate()
    }

    override fun findAllTasksByStatus(vararg status: TaskStatus): List<Task> =
        entityManager.createQuery(
            """
            SELECT t FROM TaskEntity t 
            where (t.processingInstance is null or t.processingInstance = :instanceId) 
            and t.status in :status
        """.trimIndent(),
            TaskEntity::class.java
        ).setParameter("status", status.toList())
            .setParameter("instanceId", Constants.INSTANCE_ID)
            .resultList
            .map {
                Task(
                    id = UUID.fromString(it.id),
                    stlDirectory = it.stlDirectory,
                    createdAt = it.createdAt,
                    attempts = it.attempts,
                    status = it.status,
                    lastError = it.lastError,
                    processingInstance = it.processingInstance,
                    customer = it.customer.let { customerEntity ->
                        Customer(
                            customerName = customerEntity.customerName,
                            customerPhone = customerEntity.customerPhone,
                            customerEmail = customerEntity.customerEmail,
                            customerZipCode = customerEntity.customerZipCode,
                        )
                    },
                )
            }
}