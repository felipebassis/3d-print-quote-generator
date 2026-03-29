package com.goat.infrastructure.persistence.entity

import com.goat.domain.model.TaskStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "task")
class TaskEntity(

    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "stl_directory")
    val stlDirectory: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "attempts")
    var attempts: Int,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: TaskStatus,

    @Column(name = "last_error")
    var lastError: String? = null,

    @Column(name = "processing_instance_id")
    var processingInstance: String? = null,

    @OneToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "customer_id")
    val customer: CustomerEntity,
)