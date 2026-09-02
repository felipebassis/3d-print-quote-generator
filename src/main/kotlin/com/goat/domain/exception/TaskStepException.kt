package com.goat.domain.exception

abstract class TaskStepException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)