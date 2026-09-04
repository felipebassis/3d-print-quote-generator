package com.goat.infrastructure.reader

import com.goat.domain.model.PrintInfo
import com.goat.domain.port.GCodeReader
import com.goat.infrastructure.extensions.toDuration
import jakarta.enterprise.context.ApplicationScoped
import java.math.BigDecimal
import java.nio.file.Path
import java.time.Duration
import java.util.zip.ZipFile
import kotlin.io.path.absolutePathString

@ApplicationScoped
internal class GCodeReaderImpl : GCodeReader {

    private val plateRegex = Regex("Metadata/plate_\\d+\\.gcode")
    private val plateThumbnailRegex = Regex("Metadata/plate_\\d+\\.png")
    private val estimatedPrintingTimeRegex = Regex(";\\s*total estimated time\\s*:.*")
    private val estimatedPrintingTimeValueRegex = Regex("(\\d+[\\sdhms])")
    private val filamentUsedRegex = Regex(";\\s*filament\\sused\\s\\[g]\\s*=.*")
    private val filamentUsedValueRegex = Regex("(\\d+\\.\\d+)")
    private val polygonRegex = Regex("POLYGON=(\\[\\[.*]])")
    private val pointRegex = Regex("\\[\\s*(-?\\d+(?:\\.\\d+)?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?)\\s*]")

    override fun extractPrintingInfo(gcodeFile: Path): List<PrintInfo> = ZipFile(gcodeFile.toFile()).use { file ->
        val infoForPlates = mutableListOf<PrintInfo>()
        val plateGCodeEntries = file.entries()
            .asSequence()
            .filter { it.name.matches(plateRegex) }
            .associateBy { it.name.removePrefix("Metadata/").removeSuffix(".gcode") }
        val plateThumbnailEntries = file.entries()
            .asSequence()
            .filter { it.name.matches(plateThumbnailRegex) }
            .associateBy { it.name.removePrefix("Metadata/").removeSuffix(".png") }

        if (plateGCodeEntries.isEmpty()) {
            throw IllegalStateException("G-code not found in ${gcodeFile.absolutePathString()}")
        }

        plateGCodeEntries.forEach { (entryName, entry) ->
            var estimatedPrintingTime: Duration? = null
            var filamentAmount: BigDecimal? = null
            file.getInputStream(entry).bufferedReader()
                .useLines { lines ->
                    lines.forEach { line ->
                        if (estimatedPrintingTime == null) {
                            estimatedPrintingTime = estimatedPrintingTimeRegex.find(line)
                                ?.groupValues
                                ?.get(1)
                                ?.let {
                                    estimatedPrintingTimeValueRegex.find(it)
                                        ?.groupValues
                                        ?.joinToString()
                                }
                                ?.trim()
                                ?.toDuration()
                        }
                        if (filamentAmount == null) {
                            filamentAmount = filamentUsedRegex.find(line)
                                ?.groupValues
                                ?.get(1)
                                ?.let {
                                    filamentUsedValueRegex.find(it)
                                        ?.groupValues
                                        ?.map(String::toBigDecimalOrNull)
                                }
                                ?.fold(BigDecimal.ZERO, BigDecimal::add)
                        }
                        if (estimatedPrintingTime != null && filamentAmount != null) {
                            return@useLines
                        }
                    }
                }
            infoForPlates.add(
                PrintInfo(
                    fileName = file.name,
                    plate = entry.name,
                    printDuration = estimatedPrintingTime ?: throw IllegalStateException(
                        "Estimated time not found in ${entry.name}"
                    ),
                    filamentAmount = filamentAmount ?: throw IllegalStateException(
                        "Filament amount not found in ${entry.name}"
                    ),
                    thumbnail = file.getInputStream(
                        plateThumbnailEntries[entryName]
                            ?: throw IllegalStateException("Thumbnail not found in ${entry.name}")
                    ).use { it.readAllBytes() }
                )
            )
        }
        return infoForPlates
    }
}