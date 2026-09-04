package com.goat.infrastructure.generator

import com.goat.domain.model.Quote
import com.goat.domain.port.PdfGenerator
import com.goat.infrastructure.extensions.Locales
import com.goat.infrastructure.persistence.repository.FileRepository
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.AbstractContext
import java.nio.file.Files

@ApplicationScoped
internal class ThymeleafPdfGenerator(
    private val templateEngine: TemplateEngine,
    private val fileRepository: FileRepository,
    @param:ConfigProperty(name = "file-system.template-path") private val templatePath: String,
) : PdfGenerator {

    override fun generate(quote: Quote) {
        val tempQuoteDirectory = Files.createTempDirectory("quote-directory")
        val quoteFile = tempQuoteDirectory.resolve("quote.pdf")
        try {
            quoteFile.toFile().outputStream().use {
                PdfRendererBuilder()
                    .withHtmlContent(
                        templateEngine.process(QUOTE_TEMPLATE_NAME, QuoteContext(quote)),
                        templatePath
                    ).toStream(it)
                    .run()
            }
            fileRepository.save(quote.taskId, listOf(quoteFile))
        } finally {
            Files.deleteIfExists(quoteFile)
            Files.deleteIfExists(tempQuoteDirectory)
        }
    }

    companion object {
        const val QUOTE_TEMPLATE_NAME = "quote_template"
    }
}

private class QuoteContext(quote: Quote) : AbstractContext(Locales.BRAZIL) {
    init {
        setVariable("customer-name", quote.customer.customerName)
        setVariable("customer-email", quote.customer.customerEmail)
        setVariable("customer-phone", quote.customer.customerPhone)
        setVariable("customer-zip-code", quote.customer.customerZipCode)
        setVariable("shipping-rates", quote.shippingRates)
        setVariable("quote-items", quote.items)
    }
}