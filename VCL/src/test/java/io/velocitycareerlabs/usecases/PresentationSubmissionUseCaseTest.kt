package io.velocitycareerlabs.usecases

import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.repositories.JwtServiceRepositoryImpl
import io.velocitycareerlabs.impl.data.repositories.SubmissionRepositoryImpl
import io.velocitycareerlabs.impl.data.usecases.PresentationSubmissionUseCaseImpl
import io.velocitycareerlabs.impl.domain.usecases.PresentationSubmissionUseCase
import io.velocitycareerlabs.infrastructure.EmptyExecutor
import io.velocitycareerlabs.infrastructure.network.NetworkServiceSuccess
import io.velocitycareerlabs.infrastructure.resources.valid.PresentationSubmissionMocks
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Michael Avoyan on 5/1/21.
 */
internal class PresentationSubmissionUseCaseTest {

    lateinit var subject: PresentationSubmissionUseCase

    @Before
    fun setUp() {
    }

    @Test
    fun testSubmitPresentationSuccess() {
//        Arrange
        subject = PresentationSubmissionUseCaseImpl(
                SubmissionRepositoryImpl(
                    NetworkServiceSuccess(PresentationSubmissionMocks.PresentationSubmissionResultJson)
                ),
                JwtServiceRepositoryImpl(
                        JwtServiceImpl()
                ),
                EmptyExecutor()
        )
        val presentationSubmission = VCLPresentationSubmission(
            PresentationSubmissionMocks.PresentationRequest,
            PresentationSubmissionMocks.SelectionsList
        )
        var result: VCLResult<VCLPresentationSubmissionResult>? = null

//        Action
        subject.submit(presentationSubmission){
            result = it
        }

//        Assert
        assert(result!!.data is VCLPresentationSubmissionResult)
        assert(result!!.data == expectedPresentationSubmissionResult(JSONObject(PresentationSubmissionMocks.PresentationSubmissionResultJson)))
    }

    private fun expectedPresentationSubmissionResult(jsonObj: JSONObject): VCLPresentationSubmissionResult {
        val exchangeJsonObj = jsonObj.getJSONObject(VCLPresentationSubmissionResult.KeyExchange)
        return VCLPresentationSubmissionResult(
                token = VCLToken(jsonObj.getString(VCLPresentationSubmissionResult.KeyToken)),
                exchange = expectedExchange(exchangeJsonObj)
        )
    }

    private fun expectedExchange(exchangeJsonObj: JSONObject) =
            VCLExchange(
                    id = exchangeJsonObj.getString(VCLExchange.KeyId),
                    type = exchangeJsonObj.getString(VCLExchange.KeyType),
                    disclosureComplete = exchangeJsonObj.getBoolean(VCLExchange.KeyDisclosureComplete),
                    exchangeComplete = exchangeJsonObj.getBoolean(VCLExchange.KeyExchangeComplete)
            )

    @After
    fun tearDown() {
    }
}