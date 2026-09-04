package com.goat.domain.port

import com.goat.infrastructure.config.energyoperator.EnergyOperator
import java.math.BigDecimal

interface EnergyOperatorPort {
    fun getKilowattPerHour(operator: EnergyOperator): BigDecimal

    fun isProvider(energyOperator: EnergyOperator): Boolean
}