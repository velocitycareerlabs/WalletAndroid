package io.velocitycareerlabs.infrastructure.network

import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.infrastructure.network.Response
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import java.util.*

/**
 * Created by Michael Avoyan on 4/28/21.
 */
internal class NetworkServiceSuccess(
    private val validResponse: String,
    private val isCacheValid: Boolean = false
): NetworkService {

    override fun sendRequest(
        endpoint: String,
        body: String?,
        contentType: String?,
        method: Request.HttpMethod,
        headers: List<Pair<String, String>>?,
        useCaches: Boolean,
        completionBlock: (VCLResult<Response>) -> Unit
    ) {
        completionBlock(VCLResult.Success(Response(validResponse, 0)))
    }

//    override fun isCacheValid(
//        endpoint: String,
//        method: Request.HttpMethod,
//        cacheDate: Date,
//        completionBlock: (VCLResult<Boolean>) -> Unit
//    ) {
//        completionBlock(VCLResult.Success(isCacheValid))
//    }
}