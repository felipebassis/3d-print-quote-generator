package com.goat.infrastructure.config.energyoperator

import com.goat.infrastructure.config.energyoperator.br.BrazilianEnergyOperator
import java.util.Locale

object EnergyOperatorResolver {

    fun resolve(locale: Locale, operatorName: String): EnergyOperator = when {
        locale.country.equals("BR", true) -> BrazilianEnergyOperator.fromOperatorName(operatorName)
        else -> throw IllegalArgumentException("Country not supported: ${locale.country}")
    }
}