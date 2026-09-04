package com.goat.domain.model

import java.math.BigDecimal
import java.time.Duration
import java.util.UUID

data class Quote(
    val taskId: UUID,
    val customer: Customer,
    val items: List<QuoteItem>,
    val shippingRates: List<ShippingRate>,
)

data class QuoteItem(
    val name: String,
    val printingDuration: Duration,
    val unitPrice: BigDecimal
)
