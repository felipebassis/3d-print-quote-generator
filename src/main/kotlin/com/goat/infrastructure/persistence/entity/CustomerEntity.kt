package com.goat.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "customer")
class CustomerEntity(
    @Id
    @Column(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "customer_name")
    val customerName: String,

    @Column(name = "customer_phone")
    val customerPhone: String,

    @Column(name = "customer_email")
    val customerEmail: String?,

    @Column(name = "customer_zip_code")
    val customerZipCode: String?,
)