package com.goat.infrastructure.config

import com.goat.infrastructure.Constants
import com.goat.infrastructure.config.energyoperator.EnergyOperator
import com.goat.infrastructure.config.energyoperator.EnergyOperatorResolver
import com.goat.infrastructure.config.shipping.ShippingRateProvider
import com.goat.infrastructure.config.shipping.ShippingRateResolver
import com.goat.infrastructure.extensions.Loggable
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperties
import java.math.RoundingMode
import java.util.Locale


@ApplicationScoped
internal class QuoteCalculationConfiguration : Loggable {

    @Produces
    fun quoteCalculationParameters(
        properties: QuoteCalculationProperties
    ): QuoteCalculationParameters = QuoteCalculationParameters(
        printerPowerConsumption = properties.printerPowerConsumption ?: Constants.DEFAULT_PRINTER_POWER_CONSUMPTION,
        energyOperator = properties.countryCode?.let {
            EnergyOperatorResolver.resolve(
                Locale.of(it),
                properties.energyOperatorName ?: throw IllegalStateException("Energy operator name not set"),
            )
        },
        shippingRateProvider = properties.countryCode?.let {
            ShippingRateResolver.resolve(
                Locale.of(it),
                properties.shippingRateProvider ?: throw IllegalStateException("Shipping rate provider not set")
            )
        },
        kilowattPerHourValue = if ((properties.countryCode == null || properties.energyOperatorName == null)
            && properties.kilowattPerHourValue == null
        ) throw IllegalStateException("No energy operator name set and no kilowatt per hour value provided")
        else properties.kilowattPerHourValue,
        filamentKilogramValue = properties.filamentKilogramValue
            ?: throw IllegalStateException("Filament Kilogram value not set"),
        profitMargin = properties.profitMargin ?: throw IllegalStateException("Profit margin not set"),
        roundingStrategy = when {
            "UP".equals(properties.roundingStrategy, true) -> RoundingMode.UP
            "DOWN".equals(properties.roundingStrategy, true) -> RoundingMode.DOWN
            "HALF_UP".equals(properties.roundingStrategy, true) -> RoundingMode.HALF_UP
            "HALF_DOWN".equals(properties.roundingStrategy, true) -> RoundingMode.DOWN
            "HALF_EVEN".equals(properties.roundingStrategy, true) -> RoundingMode.HALF_EVEN
            "CEILING".equals(properties.roundingStrategy, true) -> RoundingMode.CEILING
            "FLOOR".equals(properties.roundingStrategy, true) -> RoundingMode.FLOOR
            else -> throw IllegalStateException("Rounding Strategy not set")
        }
    )
}

data class QuoteCalculationParameters(
    val printerPowerConsumption: Double,
    val energyOperator: EnergyOperator?,
    val shippingRateProvider: ShippingRateProvider?,
    val kilowattPerHourValue: Double?,
    val filamentKilogramValue: Double,
    val profitMargin: Double,
    val roundingStrategy: RoundingMode
)

@ConfigProperties(prefix = "quote-parameters")
internal class QuoteCalculationProperties(
    var countryCode: String? = null,
    var energyOperatorName: String? = null,

    var shippingRateProvider: String? = null,

    var printerPowerConsumption: Double? = null,
    var kilowattPerHourValue: Double? = null,

    var filamentKilogramValue: Double? = null,

    var profitMargin: Double? = null,

    var roundingStrategy: String? = null
)