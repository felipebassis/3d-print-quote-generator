package com.goat.infrastructure.persistence.enums

import java.nio.file.Path
import kotlin.io.path.extension

enum class FileType(
    vararg extension: String,
) {
    MODELS("stl"),
    G_CODE("3mf", "gcode"),
    QUOTE("pdf"), ;

    private val extension: List<String> = extension.toList()

    fun isCompatible(file: Path): Boolean = extension.contains(file.extension)
}