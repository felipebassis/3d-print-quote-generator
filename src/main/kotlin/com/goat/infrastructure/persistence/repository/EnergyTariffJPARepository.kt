package com.goat.infrastructure.persistence.repository

import com.goat.infrastructure.Constants.YEAR_MONTH_FORMAT
import com.goat.infrastructure.config.energyoperator.EnergyOperator
import com.goat.infrastructure.extensions.Loggable
import com.goat.infrastructure.persistence.entity.EnergyTariffEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import org.hibernate.exception.ConstraintViolationException
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@ApplicationScoped
internal class EnergyTariffJPARepository(
    private val entityManager: EntityManager,
) : EnergyTariffRepository, Loggable {

    override fun save(energyTariffEntity: EnergyTariffEntity): EnergyTariffEntity = try {
        entityManager.persist(energyTariffEntity)
        energyTariffEntity
    } catch (ex: ConstraintViolationException) {
        findByOperatorAndReferenceMonth(
            energyTariffEntity.operator,
            energyTariffEntity.referenceMonth
        ) ?: throw ex
    }

    override fun findByOperatorAndReferenceMonth(
        energyOperator: EnergyOperator,
        referenceMonth: YearMonth
    ): EnergyTariffEntity? =
        findByOperatorAndReferenceMonth(energyOperator, referenceMonth)

    private fun findByOperatorAndReferenceMonth(
        energyOperator: String,
        referenceMonth: YearMonth
    ): EnergyTariffEntity? = entityManager.createQuery(
        """
                SELECT e from EnergyTariffEntity e
                where e.operator = :operator 
                    and e.referenceMonth = :referenceMonth
            """.trimIndent(), EnergyTariffEntity::class.java
    ).setParameter("operator", energyOperator)
        .setParameter("referenceMonth", referenceMonth.format(YEAR_MONTH_FORMAT))
        .singleResultOrNull
}