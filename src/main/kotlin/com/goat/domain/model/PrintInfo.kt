package com.goat.domain.model

import java.time.Duration

data class PrintInfo(
    val fileName: String,
    val printDuration: Duration,
    val filamentAmount: Double
)