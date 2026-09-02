package com.goat.infrastructure

import java.time.Duration
import java.util.UUID

object Constants {

    val INSTANCE_ID = "quote-generator-${UUID.randomUUID()}"
    val LEADER_DURATION: Duration = Duration.ofSeconds(30)
    val LAST_ALLOWED_HEARTBEAT_DURATION: Duration = Duration.ofSeconds(20)
    const val DEFAULT_PRINTER_POWER_CONSUMPTION: Double = 200.0
}