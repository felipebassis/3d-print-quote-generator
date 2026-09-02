package com.goat.domain.model

import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*

data class Task(
    val id: UUID = UUID.randomUUID(),
    val stlDirectory: Path,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var attempts: Int = 0,
    var status: TaskStatus = TaskStatus.PENDING,
    var lastError: String? = null,
    var processingInstance: String? = null,
    val customer: Customer
)
