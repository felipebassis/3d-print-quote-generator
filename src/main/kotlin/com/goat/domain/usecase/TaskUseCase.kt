package com.goat.domain.usecase

import com.goat.infrastructure.web.controller.QuoteRequestDTO

interface TaskUseCase {

    fun createTaskForGeneratingQuote(quoteRequestDTO: QuoteRequestDTO)
}