package com.goat.domain.exception

class GCodeGeneratorException(
    message: String,
    cause: Throwable? = null,
): RuntimeException(message, cause)
