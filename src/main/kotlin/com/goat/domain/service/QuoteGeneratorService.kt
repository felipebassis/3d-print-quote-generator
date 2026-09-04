package com.goat.domain.service

import com.goat.domain.model.Customer
import com.goat.domain.model.Quote
import com.goat.domain.model.QuoteItem
import com.goat.domain.port.GCodeReader
import com.goat.domain.port.PdfGenerator
import com.goat.domain.usecase.QuoteGeneratorUseCase
import com.goat.infrastructure.config.QuoteCalculationParameters
import com.goat.infrastructure.extensions.Loggable
import com.goat.infrastructure.persistence.enums.FileType
import com.goat.infrastructure.persistence.repository.FileRepository
import jakarta.enterprise.context.ApplicationScoped
import java.math.BigDecimal
import java.nio.file.Path
import java.time.Duration
import java.util.UUID

@ApplicationScoped
internal class QuoteGeneratorService(
    private val fileRepository: FileRepository,
    private val gCodeReader: GCodeReader,
    private val quoteCalculationParameters: QuoteCalculationParameters,
    private val shippingAPIResolver: ShippingAPIResolver,
    private val energyOperatorPortResolver: EnergyOperatorPortResolver,
    private val pdfGenerator: PdfGenerator
) : QuoteGeneratorUseCase, Loggable {

    override fun generateQuote(taskId: UUID, gCodeDirectoryPath: Path, customer: Customer) {
        logger.info("Finding g-code files")
        val gCodes = fileRepository.findAllFiles(gCodeDirectoryPath, FileType.G_CODE)
        logger.info("Found {} g-code files", gCodes.size)
        logger.info("Extracting printing information from files")
        val printInformation = gCodes.flatMap { gCodeReader.extractPrintingInfo(it) }
        logger.info("Printing information extracted successfully")
        logger.info("Quoting values")
        val quote = Quote(
            taskId = taskId,
            customer = customer,
            items = printInformation.map {
                QuoteItem(
                    name = it.fileName,
                    printingDuration = it.printDuration,
                    unitPrice = calculateUnitPrice(it.printDuration, it.filamentAmount, getKilowattPerHour())
                )
            },
            shippingRates = quoteCalculationParameters.shippingRateProvider
                ?.let { shippingAPIResolver.resolve(it) }
                ?.getShippingRate(customer.customerZipCode) ?: emptyList(),
        )
        logger.info("Quoting values successfully calculated")
        logger.info("Generating PDF document for quote")
        pdfGenerator.generate(quote)
        logger.info("PDF document generated successfully")
    }

    private fun calculateUnitPrice(
        printDuration: Duration,
        filamentAmount: BigDecimal,
        kilowattPerHour: BigDecimal
    ): BigDecimal {
        val costValue = quoteCalculationParameters.filamentGramValue * filamentAmount + printDuration.toMinutes()
            .toBigDecimal() * (quoteCalculationParameters.printerPowerConsumption * (kilowattPerHour / 60.toBigDecimal()))

        val profitMargin = quoteCalculationParameters.profitMargin.sortedByDescending { it.from }
            .first { it.from < costValue }
            .margin

        return (costValue * (profitMargin / 100.toBigDecimal())).setScale(
            2,
            quoteCalculationParameters.roundingStrategy
        )
    }


    private fun getKilowattPerHour(): BigDecimal =
        quoteCalculationParameters.kilowattPerHourValue
            ?: quoteCalculationParameters.energyOperator!!.let {
                energyOperatorPortResolver.resolve(it).getKilowattPerHour(it)
            }
}