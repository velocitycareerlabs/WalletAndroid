package io.velocitycareerlabs.impl.data.repositories

import io.velocitycareerlabs.api.entities.VCLCredentialTypeSchema
import io.velocitycareerlabs.api.entities.VCLError
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.domain.repositories.CredentialTypeSchemaRepository
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.impl.domain.infrastructure.db.CacheService
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import org.json.JSONObject
import java.lang.Exception
import java.util.*

/**
 * Created by Michael Avoyan on 3/31/21.
 */
internal class CredentialTypeSchemaRepositoryImpl(
        private val networkService: NetworkService,
        private val cacheService: CacheService
): CredentialTypeSchemaRepository {

    override fun getCredentialTypeSchema(
        schemaName: String, completionBlock: (VCLResult<VCLCredentialTypeSchema>) -> Unit
    ) {
        fetchCredentialTypeSchema(schemaName, completionBlock)
    }

    private fun fetchCredentialTypeSchema(
        schemaName: String, completionBlock: (VCLResult<VCLCredentialTypeSchema>) -> Unit
    ) {
        networkService.sendRequest(
            endpoint = Urls.CredentialTypeSchemas + schemaName,
            method = Request.HttpMethod.GET,
            useCaches = true,
            completionBlock = { result ->
                result.handleResult(
                    { credentialTypeSchemaResponse ->
                        try {
                            cacheService.setCredentialTypeSchema(
                                schemaName, credentialTypeSchemaResponse.payload
                            )
                            completionBlock(VCLResult.Success(
                                VCLCredentialTypeSchema(
                                    JSONObject(credentialTypeSchemaResponse.payload)
                                )
                            ))
                        } catch (ex: Exception) {
                            completionBlock(VCLResult.Failure(VCLError(ex.message)))
                        }
                    },
                    { error ->
                        completionBlock(VCLResult.Failure(error))
                    }
                )
            }
        )
    }
}