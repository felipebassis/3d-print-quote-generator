package com.goat.domain.service

import com.goat.domain.usecase.GCodeGeneratorUseCase
import com.goat.infrastructure.extensions.Loggable
import com.goat.infrastructure.generator.GCodeGenerator
import com.goat.infrastructure.persistence.repository.FileRepository
import jakarta.enterprise.context.ApplicationScoped
import java.nio.file.Path
import kotlin.time.DurationUnit
import kotlin.time.measureTime

@ApplicationScoped
internal class GCodeGeneratorService(
    private val fileRepository: FileRepository,
    private val gCodeGenerator: GCodeGenerator,
) : GCodeGeneratorUseCase, Loggable {

    override fun generateGCode(stlDirectoryPath: Path) {
        val stlFiles = fileRepository.findAllFiles(stlDirectoryPath)

        logger.info("Beginning G-code generation for {} file(s)", stlFiles.size)
        stlFiles.forEach { this.generateGCode(it, stlDirectoryPath) }
    }

    private fun generateGCode(stlFile: Path, stlDirectoryPath: Path) {
        logger.info("Generating G-code for file {}", stlFile.fileName)
        val generatedStlDuration = measureTime {
            gCodeGenerator.generateGCode(stlFile, stlDirectoryPath)
        }
        logger.info("Finished generating G-code for file {}", stlFile.fileName)
        logger.debug("Process time: {}s", generatedStlDuration.toDouble(DurationUnit.SECONDS))
    }
}