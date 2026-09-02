package com.goat.domain.model

import java.math.BigDecimal

data class ShippingRate(
    val shippingProvider: String,
    val estimatedTimeOfArrival: Int,
    val price: BigDecimal
)
