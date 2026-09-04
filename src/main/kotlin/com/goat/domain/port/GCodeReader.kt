package com.goat.domain.port

import com.goat.domain.model.PrintInfo
import java.nio.file.Path

interface GCodeReader {

    fun extractPrintingInfo(gcodeFile: Path): List<PrintInfo>
}