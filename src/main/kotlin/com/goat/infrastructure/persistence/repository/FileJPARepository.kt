package com.goat.infrastructure.persistence.repository

import com.goat.infrastructure.persistence.enums.FileType
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.extension

@ApplicationScoped
internal class FileJPARepository(
    @ConfigProperty(name = "file-system.file-directory")
    fileDirectory: String,
) : FileRepository {

    private val files: Path = Path(fileDirectory)

    override fun save(taskId: UUID, files: List<Path>): Path {
        val taskDirectory = Paths.get(this.files.absolutePathString(), taskId.toString())
        Files.createDirectories(taskDirectory)
        files.forEach {
            val persistedFile = taskDirectory.resolve("${it.fileName}.${it.extension}")
            Files.copy(it, persistedFile)
        }
        return taskDirectory
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