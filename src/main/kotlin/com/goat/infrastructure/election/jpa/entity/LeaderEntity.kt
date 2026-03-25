package com.goat.infrastructure.election.jpa.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "leader_lock")
class LeaderEntity(

    @Id
    val id: String,

    @Column(name = "current_leader")
    var currentLeader: String?,

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime?
)