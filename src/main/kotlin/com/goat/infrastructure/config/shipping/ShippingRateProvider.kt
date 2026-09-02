package com.goat.infrastructure.config.shipping

import java.util.Locale

interface ShippingRateProvider {
    val providerName: String
    val country: Locale
}