package com.goat.domain.port

import java.nio.file.Path

interface GCodeGenerator {
    fun generateGCode(stlFile: Path, stlDirectory: Path)
}