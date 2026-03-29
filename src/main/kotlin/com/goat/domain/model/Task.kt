package com.goat.domain.model

import com.goat.infrastructure.Constants
import java.time.LocalDateTime
import java.util.*

data class Task(
    val id: UUID = UUID.randomUUID(),
    val stlDirectory: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var attempts: Int = 0,
    var status: TaskStatus = TaskStatus.PENDING,
    var lastError: String? = null,
    var processingInstance: String? = null,
    val customer: Customer
)
