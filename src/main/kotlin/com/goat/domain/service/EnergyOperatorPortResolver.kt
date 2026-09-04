package com.goat.domain.service

import com.goat.domain.port.EnergyOperatorPort
import com.goat.infrastructure.config.energyoperator.EnergyOperator
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
internal class EnergyOperatorPortResolver(
    private val ports: List<EnergyOperatorPort>
) {
    fun resolve(operator: EnergyOperator): EnergyOperatorPort = ports.find { it.isProvider(operator) }
        ?: throw IllegalStateException("The operator $operator does not have a port implementation")
}