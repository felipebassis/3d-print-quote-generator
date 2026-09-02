package com.goat.infrastructure.persistence.repository

import com.goat.infrastructure.persistence.enums.FileType
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
        files.forEach {
            val persistedFile = taskStlDirectory.resolve("${it.fileName()}.stl")
            if (it.filePath() != null) {
                Files.copy(it.filePath(), persistedFile)
            } else {
                throw IllegalStateException("File path is null.")
            }
        }
        return taskStlDirectory
    }

    override fun findAllFiles(directory: Path, fileType: FileType): List<Path> {
        if (directory.exists()) {
            val files = Files.walk(directory)
                .filter(fileType::isCompatible)
                .toList()
            if (files.isEmpty()) {
                throw IllegalStateException("Directory '${directory.absolutePathString()}' is empty.")
            }
            return files
        }
        throw IllegalStateException("Directory '${directory.absolutePathString()}' does not exist.")
    }
}