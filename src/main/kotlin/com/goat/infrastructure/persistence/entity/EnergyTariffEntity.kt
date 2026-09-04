package com.goat.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.YearMonth
import java.util.UUID

@Entity
@Table(name = "energy_tariff")
class EnergyTariffEntity(
    @Id
    @Column(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "operator")
    val operator: String,

    @Column(name = "country", length = 2)
    val country: String,

    @Column(name = "tariff")
    val tariff: BigDecimal,

    @Column(name = "reference_month")
    val referenceMonth: YearMonth,
)