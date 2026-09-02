package com.goat.domain.model

import java.math.BigDecimal
import java.time.Duration

data class Quote(
    val customer: Customer,
    val items: List<QuoteItem>,
    val shippingRates: List<ShippingRate>,
) {
}

data class QuoteItem(
    val name: String,
    val printingDuration: Duration,
    val unitPrice: BigDecimal
)
