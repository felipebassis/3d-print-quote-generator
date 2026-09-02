package com.goat.domain.exception

class QuoteGeneratorException(
    message: String,
    cause: Throwable? = null,
) : TaskStepException(message, cause)
