package com.goat.infrastructure.config.energyoperator

import java.util.Locale

interface EnergyOperator {
    val operatorName: String
    val country: Locale
}