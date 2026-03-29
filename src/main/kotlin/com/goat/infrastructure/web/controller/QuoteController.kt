package com.goat.infrastructure.web.controller

import com.goat.domain.usecase.TaskUseCase
import jakarta.validation.Validator
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import jakarta.ws.rs.BeanParam
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload

@Path("/v1/quotes")
@Produces(MediaType.APPLICATION_JSON)
class QuoteController(
    private val validator: Validator,
    private val taskUseCase: TaskUseCase
) {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun createTask(
        @BeanParam quoteRequestDTO: QuoteRequestDTO,
    ): Response {
        val violations = validator.validate(quoteRequestDTO)
        if (violations.isNotEmpty()) {
            val errors = violations.map {
                "${it.propertyPath}: ${it.message}"
            }
            return Response.status(400).entity(mapOf("errors" to errors)).build()
        }
        taskUseCase.createTaskForGeneratingQuote(quoteRequestDTO)
        return Response.accepted()
            .build()
    }

}

class QuoteRequestDTO {
    @RestForm("customer-name")
    @NotEmpty(message = "Customer name is mandatory.")
    var customerName: String = ""

    @RestForm("customer-phone")
    @NotEmpty(message = "Customer phone is mandatory.")
    var customerPhone: String = ""

    @RestForm("customer-email")
    @Email(message = "Customer email is malformed.")
    var customerEmail: String? = null

    @RestForm("customer-zip-code")
    var customerZipCode: String? = null

    @RestForm("stl-files")
    @Size(min = 1, message = "At least 1 file should be uploaded.")
    var stlFiles: List<FileUpload> = emptyList()
}