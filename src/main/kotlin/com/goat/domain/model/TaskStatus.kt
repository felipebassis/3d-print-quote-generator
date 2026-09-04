package com.goat.domain.model

enum class TaskStatus {
    PENDING,
    GENERATING_G_CODE,
    GENERATING_QUOTE,
    SENDING_QUOTE,
    COMPLETED,
    ERROR,;

    fun next(): TaskStatus = when(this) {
        PENDING -> GENERATING_G_CODE
        GENERATING_G_CODE -> GENERATING_QUOTE
        GENERATING_QUOTE -> SENDING_QUOTE
        SENDING_QUOTE -> COMPLETED
        COMPLETED -> this
        ERROR -> this
    }

    open fun toError(): TaskStatus = ERROR
}