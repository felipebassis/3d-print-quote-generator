package com.goat.infrastructure.persistence.converter

import com.goat.infrastructure.Constants.YEAR_MONTH_FORMAT
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.math.BigDecimal
import java.time.YearMonth

@Converter(autoApply = true)
class YearMonthConverter : AttributeConverter<YearMonth, String> {
    override fun convertToDatabaseColumn(value: YearMonth?): String? =
        value?.format(YEAR_MONTH_FORMAT)

    override fun convertToEntityAttribute(value: String?): YearMonth? =
        value?.let { YearMonth.parse(value, YEAR_MONTH_FORMAT) }
}

@Converter(autoApply = true)
class BigDecimalConverter : AttributeConverter<BigDecimal, String> {
    override fun convertToDatabaseColumn(value: BigDecimal?): String? =
        value?.toPlainString()

    override fun convertToEntityAttribute(value: String?): BigDecimal? =
        value?.toBigDecimal()

}

