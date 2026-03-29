package com.goat.infrastructure.web.handler

import com.goat.infrastructure.extensions.Loggable
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class GlobalExceptionMapper : ExceptionMapper<Exception>, Loggable {
    override fun toResponse(exception: Exception): Response? {
        logger.error("Unexpected error", exception)
        return Response.status(500).build()
    }
}