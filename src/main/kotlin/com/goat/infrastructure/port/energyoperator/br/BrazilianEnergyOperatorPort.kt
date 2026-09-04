package com.goat.infrastructure.port.energyoperator.br

import com.goat.domain.port.EnergyOperatorPort
import com.goat.infrastructure.config.energyoperator.EnergyOperator
import com.goat.infrastructure.extensions.Locales
import com.goat.infrastructure.persistence.entity.EnergyTariffEntity
import com.goat.infrastructure.persistence.repository.EnergyTariffRepository
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.YearMonth
import java.util.*

@ApplicationScoped
internal class BrazilianEnergyOperatorPort(
    private val energyTariffRepository: EnergyTariffRepository,

    @param:RestClient
    private val aneelClient: AneelClient
) : EnergyOperatorPort {
    override fun getKilowattPerHour(operator: EnergyOperator): BigDecimal =
        energyTariffRepository.findByOperatorAndReferenceMonth(operator, YearMonth.now())
            ?.tariff
            ?: getTariffValue(operator).also {
                energyTariffRepository.save(
                    EnergyTariffEntity(
                        operator = operator.operatorName,
                        country = operator.country.country,
                        tariff = it,
                        referenceMonth = YearMonth.now()
                    )
                )
            }

    override fun isProvider(energyOperator: EnergyOperator): Boolean =
        energyOperator.country == Locales.BRAZIL

    private fun getTariffValue(operator: EnergyOperator): BigDecimal {
        val tariffValue = aneelClient.search(
            AneelDatastoreRequest(
                resourceId = TARIFF_RESOURCE_ID,
                sort = "DatInicioVigencia desc",
                filters = mapOf(
                    "SigAgente" to operator.operatorName,
                    "DscSubGrupo" to "B1",
                    "DscClasse" to "Residencial",
                    "DscSubClasse" to "Residencial",
                    "DscModalidadeTarifaria" to "Convencional",
                    "DscBaseTarifaria" to "Tarifa de Aplicação"
                )
            )
        ).result.records[0].let {
            it["VlrTUSD"]!!.toBigDecimal() + it["VlrTE"]!!.toBigDecimal()
        }

        val currentFlagValue = aneelClient.search(
            AneelDatastoreRequest(
                resourceId = FLAG_RESOURCE_ID,
                sort = "DatCompetencia desc"
            )
        ).result.records[0]["VlrAdicionalBandeira"]!!.toBigDecimal()

        return (tariffValue + currentFlagValue).divide(BigDecimal.valueOf(1000), 10, RoundingMode.HALF_EVEN)
            .setScale(5, RoundingMode.HALF_EVEN)
    }

    companion object {
        val TARIFF_RESOURCE_ID: UUID = UUID.fromString("fcf2906c-7c32-4b9b-a637-054e7a5234f4")
        val FLAG_RESOURCE_ID: UUID = UUID.fromString("0591b8f6-fe54-437b-b72b-1aa2efd46e42")
    }
}

