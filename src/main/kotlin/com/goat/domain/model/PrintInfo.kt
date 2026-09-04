package com.goat.domain.model

import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.time.Duration

data class PrintInfo(
    val fileName: String,
    val plate: String,
    val printDuration: Duration,
    val filamentAmount: BigDecimal,
    val thumbnail: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PrintInfo

        if (fileName != other.fileName) return false
        if (plate != other.plate) return false
        if (printDuration != other.printDuration) return false
        if (filamentAmount != other.filamentAmount) return false
        if (!thumbnail.contentEquals(other.thumbnail)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + plate.hashCode()
        result = 31 * result + printDuration.hashCode()
        result = 31 * result + filamentAmount.hashCode()
        result = 31 * result + thumbnail.contentHashCode()
        return result
    }
}