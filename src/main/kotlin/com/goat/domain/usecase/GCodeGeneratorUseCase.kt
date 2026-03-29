package com.goat.domain.usecase

import java.nio.file.Path

interface GCodeGeneratorUseCase {

    fun generateGCode(stlDirectoryPath: Path)
}
