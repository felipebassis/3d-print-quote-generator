package com.goat.infrastructure.persistence.repository

import com.goat.infrastructure.config.energyoperator.EnergyOperator
import com.goat.infrastructure.persistence.entity.EnergyTariffEntity
import java.time.YearMonth

interface EnergyTariffRepository {

    fun save(energyTariffEntity: EnergyTariffEntity): EnergyTariffEntity
    fun findByOperatorAndReferenceMonth(energyOperator: EnergyOperator, referenceMonth: YearMonth) : EnergyTariffEntity?
}