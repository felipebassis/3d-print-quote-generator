package com.goat.infrastructure.config.shipping

import com.goat.infrastructure.config.shipping.bra.BrazilianShippingRateProvider
import java.util.Locale

object ShippingRateResolver {
    fun resolve(locale: Locale, shippingRateProvider: String): ShippingRateProvider =
        when {
            locale.country.equals("BR", true) -> BrazilianShippingRateProvider.fromProviderName(shippingRateProvider)
            else -> throw IllegalArgumentException("Country not supported: ${locale.country}")
        }
}