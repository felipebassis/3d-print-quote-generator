package com.goat.infrastructure.persistence.entity

import com.goat.infrastructure.persistence.enums.InstanceStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "instance")
class InstanceEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(name = "instance_id",nullable = false)
    val instanceId: String,

    @Column(name = "last_heartbeat")
    val lastHeartbeat: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: InstanceStatus = InstanceStatus.ALIVE,
)
