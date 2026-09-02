package com.goat.domain.service

import com.goat.domain.model.Customer
import com.goat.domain.model.Quote
import com.goat.domain.model.QuoteItem
import com.goat.domain.port.GCodeReader
import com.goat.domain.port.PdfGenerator
import com.goat.domain.usecase.QuoteGeneratorUseCase
import com.goat.infrastructure.config.QuoteCalculationParameters
import com.goat.infrastructure.persistence.enums.FileType
import com.goat.infrastructure.persistence.repository.FileRepository
import jakarta.enterprise.context.ApplicationScoped
import java.math.BigDecimal
import java.nio.file.Path
import java.time.Duration

@ApplicationScoped
internal class QuoteGeneratorService(
    private val fileRepository: FileRepository,
    private val gCodeReader: GCodeReader,
    private val quoteCalculationParameters: QuoteCalculationParameters,
    private val shippingAPIResolver: ShippingAPIResolver,
    private val pdfGenerator: PdfGenerator
) : QuoteGeneratorUseCase {

    override fun generateQuote(gCodeDirectoryPath: Path, customer: Customer) {
        val gCodes = fileRepository.findAllFiles(gCodeDirectoryPath, FileType.G_CODE)
        val printInformation = gCodes.map { gCodeReader.extractPrintingInfo(it) }
        val quote = Quote(
            customer = customer,
            items = printInformation.map {
                QuoteItem(
                    name = it.fileName,
                    printingDuration = it.printDuration,
                    unitPrice = calculateUnitPrice(it.printDuration, it.filamentAmount)
                )
            },
            shippingRates = quoteCalculationParameters.shippingRateProvider
                ?.let { shippingAPIResolver.resolve(it) }
                ?.getShippingRate(customer.customerZipCode) ?: emptyList(),

        )
        pdfGenerator.generate()
    }

    private fun calculateUnitPrice(printDuration: Duration, filamentAmount: Double): BigDecimal {

    }
}