package com.goat.infrastructure.persistence.repository

import com.goat.infrastructure.persistence.enums.FileType
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.nio.file.Path
import java.util.*

interface FileRepository {

    fun save(taskId: UUID, files: List<FileUpload>): Path
    fun findAllFiles(directory: Path, fileType: FileType): List<Path>
}