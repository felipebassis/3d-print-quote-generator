package com.goat.infrastructure.election.jpa.repository

import com.goat.infrastructure.Constants.INSTANCE_ID
import com.goat.infrastructure.election.jpa.entity.InstanceEntity
import com.goat.infrastructure.election.jpa.entity.InstanceStatus
import jakarta.inject.Singleton
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@Singleton
internal class InstanceRepositoryImpl(
    private val entityManager: EntityManager,
) : InstanceRepository {

    @Transactional(Transactional.TxType.REQUIRED)
    override fun save(instance: InstanceEntity) =
        entityManager.persist(instance)

    @Transactional(Transactional.TxType.REQUIRED)
    override fun updateHeartBeat() {
        entityManager.createQuery(
            """
                UPDATE InstanceEntity i SET 
                    i.lastHeartbeat = :now
                WHERE i.instanceId = :instanceId
            """.trimIndent()
        ).setParameter("instanceId", INSTANCE_ID)
            .setParameter("now", LocalDateTime.now())
            .executeUpdate()
    }

    @Transactional(Transactional.TxType.REQUIRED)
    override fun markAsShuttingDown() {
        entityManager.createQuery(
            """
                UPDATE InstanceEntity i SET 
                    i.status = :status
                WHERE i.instanceId = :instanceId
            """.trimIndent()
        ).setParameter("instanceId", INSTANCE_ID)
            .setParameter("status", InstanceStatus.SHUTTING_DOWN)
            .executeUpdate()
    }

    override fun findAllNotDead(): List<InstanceEntity> =
        entityManager.createQuery(
            """
            SELECT i FROM InstanceEntity i 
                WHERE i.status <> com.goat.infrastructure.election.jpa.entity.InstanceStatus.DEAD
                ORDER BY i.id
        """.trimIndent(), InstanceEntity::class.java
        ).resultList
}