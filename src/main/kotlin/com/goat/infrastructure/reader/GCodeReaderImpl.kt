package com.goat.infrastructure.reader

import com.goat.domain.model.PrintInfo
import com.goat.domain.port.GCodeReader
import com.goat.infrastructure.extensions.toDuration
import jakarta.enterprise.context.ApplicationScoped
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

@ApplicationScoped
internal class GCodeReaderImpl : GCodeReader {

    private val estimatedPrintingTimeRegex = Regex(";\\s*total estimated time\\s*:\\s*([\\d\\sdhms]+)")
    private val filamentUsedRegex = Regex(";\\s*filament\\sused\\s\\[g]\\s*=\\s*(\\d+)\\.(\\d+)")

    override fun extractPrintingInfo(gcodeFile: Path): PrintInfo = ZipFile(gcodeFile.toFile()).use {
        val gCode = it.getEntry("Metadata/plate_1.gcode")
        if (gCode != null) {
            it.getInputStream(gCode).bufferedReader().use { reader ->
                val fileContent = reader.readText()
                val estimatedPrintingTime = estimatedPrintingTimeRegex.find(fileContent)
                    ?.groupValues
                    ?.get(1)
                    ?.trim()
                    ?.toDuration() ?: throw IllegalStateException("Estimated time not found in G-code in ${gcodeFile.absolutePathString()}")
                val filamentAmount = filamentUsedRegex.find(fileContent)
                    ?.groupValues
                    ?.get(1)
                    ?.toDoubleOrNull()
                    ?: throw IllegalStateException("Filament used not found in G-code in ${gcodeFile.absolutePathString()}")
                return PrintInfo(
                    fileName = gcodeFile.name,
                    printDuration = estimatedPrintingTime,
                    filamentAmount = filamentAmount,
                )
            }
        }
        throw IllegalStateException("G-code not found in ${gcodeFile.absolutePathString()}")
    }
}