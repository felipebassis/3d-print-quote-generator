package com.goat.infrastructure

import com.goat.infrastructure.generator.ThymeleafPdfGenerator.Companion.QUOTE_TEMPLATE_NAME
import java.math.BigDecimal
import java.text.MessageFormat
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.UUID

object Constants {

    val INSTANCE_ID = "quote-generator-${UUID.randomUUID()}"
    val LEADER_DURATION: Duration = Duration.ofSeconds(30)
    val LAST_ALLOWED_HEARTBEAT_DURATION: Duration = Duration.ofSeconds(20)
    val DEFAULT_PRINTER_POWER_CONSUMPTION: BigDecimal = BigDecimal.valueOf(200.0)
    val YEAR_MONTH_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val QUOTE_FILE_NAME: String = "quote.pdf"
}