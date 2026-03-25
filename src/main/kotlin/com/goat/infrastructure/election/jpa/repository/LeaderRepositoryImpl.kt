package com.goat.infrastructure.election.jpa.repository

import com.goat.infrastructure.Constants
import com.goat.infrastructure.Constants.INSTANCE_ID
import com.goat.infrastructure.Constants.LEADER_DURATION
import jakarta.inject.Singleton
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@Singleton
internal class LeaderRepositoryImpl(
    private val entityManager: EntityManager
) : LeaderRepository {

    @Transactional(Transactional.TxType.REQUIRED)
    override fun updateToCurrentLeader(): Boolean =
        entityManager.createQuery(
            """
            update LeaderEntity le set 
                le.currentLeader = :instanceId, 
                le.expiresAt = :expiration
            where le.expiresAt < :now 
                or le.currentLeader = :instanceId 
                """.trimMargin()
        ).setParameter("instanceId", INSTANCE_ID)
            .setParameter("now", LocalDateTime.now())
            .setParameter("expiration", LocalDateTime.now().plus(LEADER_DURATION))
            .executeUpdate() > 0

    override fun isCurrentLeader(): Boolean =
        entityManager.createQuery(
            """
                select count(*) from LeaderEntity 
                where currentLeader = :instanceId
            """.trimIndent(), Long::class.java
        ).setParameter("instanceId", INSTANCE_ID)
            .singleResult > 0
}