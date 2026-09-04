package com.goat.domain.port

import com.goat.domain.model.Quote

interface PdfGenerator {

    fun generate(quote: Quote)
}