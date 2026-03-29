package com.goat.domain.model

data class Customer(
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String?,
    val customerZipCode: String?,
)