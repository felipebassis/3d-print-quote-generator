package com.goat.domain.usecase

import com.goat.domain.model.Customer
import java.nio.file.Path

interface QuoteGeneratorUseCase {

    fun generateQuote(gCodeDirectoryPath: Path, customer: Customer)
}