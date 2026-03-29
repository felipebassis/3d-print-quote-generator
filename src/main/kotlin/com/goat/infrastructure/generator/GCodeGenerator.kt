package com.goat.infrastructure.generator

import com.goat.infrastructure.extensions.Loggable
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.extension
import kotlin.io.path.notExists
import kotlin.io.path.toPath

@ApplicationScoped
class GCodeGenerator(
    @ConfigProperty(name = "slicer.path")
    slicerPath: Optional<String>,
    @ConfigProperty(name = "slicer.config")
    slicerConfig: Optional<String>,
) : Loggable {

    private val slicerPath = Path(
        slicerPath.orElse(
            Thread.currentThread()
                .contextClassLoader
                .getResource("/slicer/OrcaSlicer_Linux_AppImage_Ubuntu2404_V2.3.1.AppImage")
                ?.toURI()
                ?.toPath()
                ?.absolutePathString()
                ?: throw IllegalStateException("Slicer not found in project.")
        )
    )
    private val slicerConfig = Path(
        slicerConfig.orElse(
            Thread.currentThread()
                .contextClassLoader
                .getResource("/config/config.3mf")
                ?.toURI()
                ?.toPath()
                ?.absolutePathString()
                ?: throw IllegalStateException("Configuration for slicer not found.")
        )
    )

    init {
        if (this.slicerPath.notExists()) {
            throw IllegalArgumentException("No slicer found in '${this.slicerPath.absolutePathString()}'")
        }
        if (this.slicerConfig.notExists()) {
            throw IllegalArgumentException("No slicer configuration found in '${this.slicerConfig.absolutePathString()}'")
        }
        if (this.slicerPath.extension != "AppImage") {
            throw IllegalArgumentException("Slicer must be 'AppImage'.")
        }
        if (this.slicerConfig.extension != "3mf") {
            throw IllegalArgumentException("Slicer configuration file must be '3mf'.")
        }
    }


    fun generateGCode(stlFile: Path, stlDirectory: Path) {
        val outputFile = File("$stlDirectory/${stlFile.fileName}.3mf")
        outputFile.createNewFile()
        logger.debug("Starting Orca-Slicer process for stl {}", stlFile.fileName)

        val processoGeracaoGCode = ProcessBuilder(
            "xvfb-run", "-a",
            slicerPath.absolutePathString(),
            "--arrange", "1",
            "--orient", "1",
            "--slice", "0",
            "--no-export-metadata",
            "--export-3mf", outputFile.absolutePath,
            slicerConfig.absolutePathString(),
            stlFile.absolutePathString()
        )
        try {
            if (processoGeracaoGCode.start()
                    .waitFor() == 0
            ) {
                logger.info("Orca-Slicer process finished successfully.")
                Files.deleteIfExists(stlFile)
            }
        } catch (ex: Exception) {
            logger.error("Error in processing slicer code", ex)
            throw ex
        }
    }

}