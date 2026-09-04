package com.goat.infrastructure.config.shipping.bra

import com.goat.infrastructure.config.shipping.ShippingRateProvider
import com.goat.infrastructure.extensions.Locales
import java.util.Locale

enum class BrazilianShippingRateProvider(
    override val providerName: String,
    override val country: Locale = Locales.BRAZIL,
) : ShippingRateProvider {
    MELHOR_ENVIO("MELHOR_ENVIO");

    companion object {
        fun fromProviderName(operatorName: String): BrazilianShippingRateProvider =
            entries.firstOrNull { it.providerName == operatorName }
                ?: throw IllegalArgumentException("Unknown shipping rate provider: $operatorName")
    }
}