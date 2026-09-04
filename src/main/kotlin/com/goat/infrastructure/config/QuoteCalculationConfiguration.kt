package com.goat.infrastructure.config

import com.goat.infrastructure.Constants
import com.goat.infrastructure.config.energyoperator.EnergyOperator
import com.goat.infrastructure.config.energyoperator.EnergyOperatorResolver
import com.goat.infrastructure.config.shipping.ShippingRateProvider
import com.goat.infrastructure.config.shipping.ShippingRateResolver
import com.goat.infrastructure.extensions.Loggable
import io.smallrye.config.ConfigMapping
import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.spi.Converter
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale


@ApplicationScoped
internal class QuoteCalculationConfiguration : Loggable {

    @Produces
    fun quoteCalculationParameters(
        properties: QuoteCalculationProperties
    ): QuoteCalculationParameters = QuoteCalculationParameters(
        printerPowerConsumption = properties.printerPowerConsumption() ?: Constants.DEFAULT_PRINTER_POWER_CONSUMPTION,
        energyOperator = properties.countryCode()?.let {
            EnergyOperatorResolver.resolve(
                Locale.of(it),
                properties.energyOperatorName() ?: throw IllegalStateException("Energy operator name not set"),
            )
        },
        shippingRateProvider = properties.countryCode()?.let {
            ShippingRateResolver.resolve(
                Locale.of(it),
                properties.shippingRateProvider() ?: throw IllegalStateException("Shipping rate provider not set")
            )
        },
        kilowattPerHourValue = if ((properties.countryCode() == null || properties.energyOperatorName() == null)
            && properties.kilowattPerHourValue() == null
        ) throw IllegalStateException("No energy operator name set and no kilowatt per hour value provided")
        else properties.kilowattPerHourValue(),
        filamentGramValue = properties.filamentKilogramValue()?.divide(1000.toBigDecimal())
            ?: throw IllegalStateException("Filament Kilogram value not set"),
        profitMargin = properties.profitMargin()?.map {
            ProfitMargin(
                it.from ?: throw IllegalArgumentException("Profit margin 'from' value not set"),
                it.margin ?: throw IllegalArgumentException("Profit margin value not set"),
            )
        } ?: throw IllegalStateException("Profit margin not set"),
        roundingStrategy = when {
            "UP".equals(properties.roundingStrategy(), true) -> RoundingMode.UP
            "DOWN".equals(properties.roundingStrategy(), true) -> RoundingMode.DOWN
            "HALF_UP".equals(properties.roundingStrategy(), true) -> RoundingMode.HALF_UP
            "HALF_DOWN".equals(properties.roundingStrategy(), true) -> RoundingMode.DOWN
            "HALF_EVEN".equals(properties.roundingStrategy(), true) -> RoundingMode.HALF_EVEN
            "CEILING".equals(properties.roundingStrategy(), true) -> RoundingMode.CEILING
            "FLOOR".equals(properties.roundingStrategy(), true) -> RoundingMode.FLOOR
            else -> throw IllegalStateException("Rounding Strategy not set")
        }
    )
}

data class QuoteCalculationParameters(
    val printerPowerConsumption: BigDecimal,
    val energyOperator: EnergyOperator?,
    val shippingRateProvider: ShippingRateProvider?,
    val kilowattPerHourValue: BigDecimal?,
    val filamentGramValue: BigDecimal,
    val profitMargin: List<ProfitMargin>,
    val roundingStrategy: RoundingMode
)

data class ProfitMargin(
    var from: BigDecimal,
    var margin: BigDecimal,
)

@ConfigMapping(prefix = "quote-parameters")
internal interface QuoteCalculationProperties {
    fun countryCode(): String?
    fun energyOperatorName(): String?

    fun shippingRateProvider(): String?

    fun printerPowerConsumption(): BigDecimal?
    fun kilowattPerHourValue(): BigDecimal?

    fun filamentKilogramValue(): BigDecimal?

    fun profitMargin(): List<ProfitMarginProperties>?

    fun roundingStrategy(): String?
}

data class ProfitMarginProperties(
    var from: BigDecimal?,
    var margin: BigDecimal?,
)

@Priority(100)
internal class ProfitMarginConverter : Converter<List<ProfitMarginProperties>> {
    override fun convert(margins: String?): List<ProfitMarginProperties>? =
        margins?.split(";")
            ?.map {
                val (from, margin) = it.split(":")
                ProfitMarginProperties(from.toBigDecimal(), margin.toBigDecimal())
            }
}