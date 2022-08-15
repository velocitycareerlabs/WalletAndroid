package io.velocitycareerlabs.impl.data.infrastructure.network

import android.accounts.NetworkErrorException
import io.velocitycareerlabs.api.entities.VCLError
import io.velocitycareerlabs.api.entities.VCLErrorCodes
import io.velocitycareerlabs.api.entities.VCLResult
import io.velocitycareerlabs.impl.GlobalConfig
import io.velocitycareerlabs.impl.domain.infrastructure.network.NetworkService
import io.velocitycareerlabs.impl.extensions.convertToString
import io.velocitycareerlabs.impl.extensions.decode
import io.velocitycareerlabs.impl.extensions.equalsTo
import io.velocitycareerlabs.impl.extensions.toDate
import io.velocitycareerlabs.impl.utils.VCLLog
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * Created by Michael Avoyan on 3/11/21.
 */
internal class NetworkServiceImpl: NetworkService {
    private val TAG = NetworkServiceImpl::class.simpleName

    override fun sendRequest(
            endpoint: String,
            body: String?,
            contentType: String?,
            method: Request.HttpMethod,
            headers: List<Pair<String, String>>?,
            useCaches: Boolean,
            completionBlock: (VCLResult<Response>) -> Unit
    ) {

//        val endpointToSend = if(method == Request.HttpMethod.GET) {
//            val builder: Uri.Builder = Uri.parse(endpoint).buildUpon()
//            builder.encodedQuery(body).build().toString()
//        }else {
//            endpoint
//        }

        sendRequest(
            Request.Builder()
                .setEndpoint(endpoint)
                .setBody(body)
                .setMethod(method)
                .setDoOutput(method == Request.HttpMethod.POST)
                .addHeaders(headers)
                .setContentType(contentType)
                .setUseCaches(useCaches)
                .build(),
            completionBlock
        )
    }

    private fun sendRequest(request: Request, completionBlock: (VCLResult<Response>) -> Unit) {
        logRequest(request)
        var connection: HttpURLConnection? = null
        try {
            connection = createConnection(request)

            writePostBody(connection, request)

            if (connection.responseCode in HttpURLConnection.HTTP_OK..299) {
                val response = Response(
                    payload = connection.inputStream.convertToString(request.encoding),
                    code = connection.responseCode
                )
                completionBlock(VCLResult.Success(response))
            } else {
                val errorMessageStream = connection.errorStream ?: connection.inputStream
                completionBlock(VCLResult.Failure(VCLError(errorMessageStream.convertToString(request.encoding), connection.responseCode)))
            }
        } catch (ex: NetworkErrorException) {
            completionBlock(VCLResult.Failure(VCLError(ex.message, VCLErrorCodes.NetworkError.value)))
        } catch (ex: UnknownHostException) {
            completionBlock(VCLResult.Failure(VCLError(ex.message, VCLErrorCodes.NetworkError.value)))
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex.message)))
        } finally {
            connection?.disconnect()
        }
    }

    private fun writePostBody(connection: HttpURLConnection, request: Request) {
        if (connection.requestMethod == Request.HttpMethod.POST.value) {
            request.body?.let { data ->
                val out = BufferedOutputStream(connection.outputStream)
                val writer = BufferedWriter(OutputStreamWriter(out, request.encoding))
                writer.write(data)
                writer.flush()

//            connection.outputStream.write(data.toByteArray(StandardCharsets.UTF_8))
//            connection.outputStream.flush()
            }
        }

    }

    private fun setHeaders(connection: HttpURLConnection, headers: List<Pair<String, String>>) {
        for ((first, second) in headers) {
            connection.setRequestProperty(first, second)
        }
    }

    /**
     * Returns new connection. referred to by given url.
     */
    private fun createConnection(request: Request): HttpURLConnection {
        val connection = if (request.endpoint.startsWith("https")) {
            URL(request.endpoint).openConnection() as HttpsURLConnection
        } else {
            URL(request.endpoint).openConnection() as HttpURLConnection
        }
        connection.connectTimeout = request.connectTimeOut
        connection.readTimeout = request.readTimeOut
        connection.requestMethod = request.method.value
        connection.doOutput = request.doOutput
        connection.doInput = request.doInput
        connection.useCaches = request.useCaches

        request.contentType?.let { connection.addRequestProperty("Content-Type", it) }

        request.headers?.let { headers -> setHeaders(connection, headers) }

        return connection
    }

//    override fun isCacheValid(endpoint: String,
//                              method: Request.HttpMethod,
//                              cacheDate: Date,
//                              completionBlock: (VCLResult<Boolean>) -> Unit) {
//        if(GlobalConfig.IsToLoadFromCacheInitialization) {
//            isCacheValid(
//                Request.Builder()
//                    .setEndpoint(endpoint)
//                    .setBody(null)
//                    .setMethod(method)
//                    .setDoOutput(false)
//                    .addHeaders(null)
//                    .setContentType(null)
//                    .build(),
//                cacheDate,
//                completionBlock
//            )
//        } else {
//            completionBlock(VCLResult.Success(false))
//        }
//    }

    private fun isCacheValid(request: Request, cacheDate: Date, completionBlock: (VCLResult<Boolean>) -> Unit) {
        var connection: HttpURLConnection? = null
        try {
            connection = createConnection(request)

            if (connection.responseCode in HttpURLConnection.HTTP_OK..299) {
                completionBlock(VCLResult.Success(
                    connection.getHeaderField("Date").toDate()?.equalsTo(cacheDate) == true
                ))
            } else {
                val errorMessageStream = connection.errorStream ?: connection.inputStream
                completionBlock(VCLResult.Failure(VCLError(errorMessageStream.convertToString(request.encoding), connection.responseCode)))
            }
        } catch (ex: Exception) {
            completionBlock(VCLResult.Failure(VCLError(ex.message)))
        } finally {
            connection?.disconnect()
        }
    }

    private fun logRequest(request: Request) {
        val methodLog = "Request Method: ${request.method}"
        val endpointLog = "\nRequest Endpoint: ${request.endpoint}"
        val bodyLog = request.body?.let { "\nRequest Body: $it" }
            ?: "\n"
        VCLLog.d(TAG, "$methodLog$endpointLog$bodyLog")
    }
}