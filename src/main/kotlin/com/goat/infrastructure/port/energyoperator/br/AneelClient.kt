package com.goat.infrastructure.port.energyoperator.br

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import java.util.UUID

@Path("api/v3/action")
@RegisterRestClient(configKey = "aneel")
internal interface AneelClient {

    @POST
    @Path("/datastore_search")
    fun search(aneelDatastoreRequest: AneelDatastoreRequest): AneelDatastoreResponse
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
internal data class AneelDatastoreRequest(
    val resourceId: UUID,
    val sort: String,
    val filters: Map<String, String> = mapOf()
) {
    @Suppress("unused")
    val limit = 1
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
internal data class AneelDatastoreResponse(
    val result: AneelDatastoreResult
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
internal data class AneelDatastoreResult(
    val records: List<Map<String, String>>
)
