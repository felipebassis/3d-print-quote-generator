package com.goat.infrastructure.persistence.repository

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

@ApplicationScoped
internal class FileRepositoryImpl(
    @ConfigProperty(name = "file-system.stl-file-directory")
    stlFileDirectory: String,
) : FileRepository {

    private val stlFile: Path = Path(stlFileDirectory)

    override fun save(taskId: UUID, files: List<FileUpload>): Path {
        val taskStlDirectory = Paths.get(stlFile.absolutePathString(), taskId.toString())
        Files.createDirectories(taskStlDirectory)
        files.forEachIndexed { index, file ->
            val persistedFile = taskStlDirectory.resolve("stl-${file.fileName()}-$index.stl")
            if (file.filePath() != null) {
                Files.copy(file.filePath(), persistedFile)
            } else {
                throw IllegalStateException("File path is null.")
            }
        }
        return taskStlDirectory
    }

    override fun findAllFiles(stlDirectory: Path): List<Path> {
        if (stlDirectory.exists()) {
            val stlFiles = Files.walk(stlDirectory)
                .filter {
                    it.toFile().extension == "stl"
                }
                .toList()
            if (stlFiles.isEmpty()) {
                throw IllegalStateException("Stl directory '${stlDirectory.absolutePathString()}' is empty.")
            }
            return stlFiles
        }
        throw IllegalStateException("Stl directory '${stlDirectory.absolutePathString()}' does not exist.")
    }
}