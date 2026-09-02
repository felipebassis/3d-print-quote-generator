package com.goat.domain.service

import com.goat.domain.port.ShippingAPIPort
import com.goat.infrastructure.config.QuoteCalculationParameters
import com.goat.infrastructure.config.shipping.ShippingRateProvider
import jakarta.enterprise.context.ApplicationScoped
import java.math.BigDecimal

@ApplicationScoped
internal class ShippingAPIResolver(
    private val ports: List<ShippingAPIPort>
) {
    fun resolve(provider: ShippingRateProvider): ShippingAPIPort = ports.find { it.provider() == provider }
        ?: throw IllegalStateException("The provider $provider does not have a port implementation")

}
