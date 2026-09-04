package com.goat.domain.usecase

import com.goat.domain.model.Customer
import java.nio.file.Path
import java.util.UUID

interface QuoteGeneratorUseCase {

    fun generateQuote(taskId: UUID, gCodeDirectoryPath: Path, customer: Customer)
}