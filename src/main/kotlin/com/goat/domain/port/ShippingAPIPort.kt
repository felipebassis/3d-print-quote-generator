package com.goat.domain.port

import com.goat.domain.model.ShippingRate
import com.goat.infrastructure.config.shipping.ShippingRateProvider

interface ShippingAPIPort {

    fun getShippingRate(zipCode: String): List<ShippingRate>

    fun provider(): ShippingRateProvider
}
