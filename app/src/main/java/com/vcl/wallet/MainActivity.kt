/**
 * Created by Michael Avoyan on 18/07/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vcl.wallet

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.vcl.wallet.databinding.ActivityMainBinding
import io.velocitycareerlabs.api.VCL
import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.api.VCLProvider
import io.velocitycareerlabs.api.VCLSignatureAlgorithm
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.initialization.VCLInitializationDescriptor
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    private lateinit var binding: ActivityMainBinding

    private val environment = VCLEnvironment.Staging

    private lateinit var vcl: VCL
    private lateinit var didJwk: VCLDidJwk

    private val didJwkDescriptor =
        VCLDidJwkDescriptor(signatureAlgorithm = VCLSignatureAlgorithm.SECP256k1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vcl = VCLProvider.vclInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.disclosingCredentials.setOnClickListener {
            getPresentationRequest()
        }
        binding.receivingCredentialsByDeeplink.setOnClickListener {
            getCredentialManifestByDeepLink()
        }
        binding.receivingCredentialsByServices.setOnClickListener {
            getOrganizationsThenCredentialManifestByService()
        }
        binding.selfReportingCredentials.setOnClickListener {
            getCredentialTypesUIFormSchema()
        }
        binding.refreshCredentials.isEnabled = false
        binding.refreshCredentials.setTextColor(Color.GRAY)
        binding.refreshCredentials.setOnClickListener {
            refreshCredentials()
        }
        binding.getVerifiedProfile.setOnClickListener {
            getVerifiedProfile()
        }
        binding.verifyJwt.setOnClickListener {
            verifyJwt()
        }
        binding.generateSignedJwt.setOnClickListener {
            generateSignedJwt()
        }
        binding.generateDidJwk.setOnClickListener {
            generateDidJwk()
        }
//        val initializationDescriptor = VCLInitializationDescriptor(
//            environment = environment,
//            xVnfProtocolVersion = VCLXVnfProtocolVersion.XVnfProtocolVersion2,
//            cryptoServicesDescriptor = VCLCryptoServicesDescriptor(
//                cryptoServiceType = VCLCryptoServiceType.Remote,
//                remoteCryptoServicesUrlsDescriptor = VCLRemoteCryptoServicesUrlsDescriptor(
//                    keyServiceUrls = VCLKeyServiceUrls(
//                        createDidKeyServiceUrl = Constants.getCreateDidKeyServiceUrl(environment = environment)
//                    ),
//                    jwtServiceUrls = VCLJwtServiceUrls(
//                        jwtSignServiceUrl = Constants.getJwtSignServiceUrl(environment = environment),
//                        jwtVerifyServiceUrl = Constants.getJwtVerifyServiceUrl(environment = environment)
//                    )
//                )
//            )
//        )
        val initializationDescriptor = VCLInitializationDescriptor(
            environment = environment
        )
        vcl.initialize(
            context = this.applicationContext,
            initializationDescriptor = initializationDescriptor,
            successHandler = {
                Log.d(TAG, "VCL Initialization succeed!")

                vcl.generateDidJwk(
                    didJwkDescriptor = didJwkDescriptor,
                    successHandler = { didJwk ->
                        this.didJwk = didJwk
                        Log.d(
                            TAG,
                            "VCL DID:JWK generated: \ndid: ${didJwk.did}\nkid: ${didJwk.kid}\nkeyId: ${didJwk.keyId}\npublicJwk: ${didJwk.publicJwk.valueStr}"
                        )
                        showControls()
                    },
                    errorHandler = { error ->
                        logError("VCL Failed to generate did:jwk with error:", error)
                        showError()
                    }
                )
            },
            errorHandler = { error ->
                logError("VCL Initialization failed with error:", error)
                showError()
            }
        )
    }

    private fun showControls() {
        binding.loadingIndicator.isVisible = false
        binding.controlsView.isVisible = true
    }

    private fun showError() {
        binding.loadingIndicator.isVisible = false
        binding.errorView.isVisible = true
    }

    private fun getPresentationRequest() {
        val deepLink =
            if (environment == VCLEnvironment.Dev)
                VCLDeepLink(Constants.PresentationRequestDeepLinkStrDev)
            else
                VCLDeepLink(Constants.PresentationRequestDeepLinkStrStaging)
        vcl.getPresentationRequest(
            presentationRequestDescriptor = VCLPresentationRequestDescriptor(
                deepLink = deepLink,
                pushDelegate = VCLPushDelegate(
                    pushUrl = "pushUrl",
                    pushToken = "pushToken"
                ),
                didJwk = this.didJwk
            ),
            successHandler = { presentationRequest ->
                Log.d(TAG, "VCL Presentation request received: ${presentationRequest.jwt.payload}")
//                Log.d(TAG, "VCL Presentation request received")

                if (presentationRequest.feed) {
                    vcl.getAuthToken(
                        VCLAuthTokenDescriptor(presentationRequest),
                        successHandler = {
                            Log.d(TAG, "auth token: ${it.payload}")
                            submitPresentation(presentationRequest, it)
                        },
                        errorHandler = {
                            Log.e(TAG, "getAuthToken failed: $it")
                        })
                } else {
                    submitPresentation(presentationRequest)
                }
            },
            errorHandler = { error ->
                logError("VCL Presentation request failed:", error)
            })
    }

    private fun submitPresentation(
        presentationRequest: VCLPresentationRequest,
        authToken: VCLAuthToken? = null
    ) {
        val presentationSubmission = VCLPresentationSubmission(
            presentationRequest = presentationRequest,
            verifiableCredentials = Constants.getIdentificationList(environment)
        )
        submitPresentation(presentationSubmission, authToken)
    }

    private fun submitPresentation(
        presentationSubmission: VCLPresentationSubmission,
        authToken: VCLAuthToken? = null
    ) {
        var authTokenRefreshAmount = 0
        vcl.submitPresentation(
            presentationSubmission = presentationSubmission,
            authToken = authToken,
            successHandler = { presentationSubmissionResult ->
                Log.d(TAG, "VCL Presentation submission result: $presentationSubmissionResult")
                vcl.getExchangeProgress(
                    VCLExchangeDescriptor(
                        presentationSubmission,
                        presentationSubmissionResult
                    ),
                    successHandler = { exchange ->
                        Log.d(TAG, "VCL Presentation exchange progress $exchange")
                    },
                    errorHandler = { error ->
                        logError("VCL Presentation exchange progress failed:", error)
                    })
            },
            errorHandler = { error ->
                logError("VCL Presentation submission failed:", error)
                if (error.statusCode == 401 && authTokenRefreshAmount == 0) {
                    authTokenRefreshAmount++
                    vcl.getAuthToken(
                        VCLAuthTokenDescriptor(
                            authTokenUri = authToken?.authTokenUri ?: "",
                            refreshToken = authToken?.refreshToken?.value,
                            walletDid = authToken?.walletDid,
                            relyingPartyDid = authToken?.relyingPartyDid,
                        ),
                        successHandler = { newAuthToken ->
                            Log.d(TAG, "auth token: ${newAuthToken.payload}");
                            vcl.submitPresentation(
                                presentationSubmission,
                                newAuthToken, successHandler = {
                                    vcl.getExchangeProgress(
                                        VCLExchangeDescriptor(
                                            presentationSubmission,
                                            it
                                        ),
                                        { exchange ->
                                            Log.d(
                                                TAG,
                                                "VCL Presentation exchange progress $exchange"
                                            )
                                        },
                                        errorHandler = { error ->
                                            logError("VCL Presentation exchange progress failed: ", error)
                                        })
                                }, errorHandler = {
                                    logError("VCL Presentation submission failed:", it)
                                })
                        },
                        errorHandler = {
                            Log.e(TAG, "getAuthToken failed: $it")
                        })
                }
            })
    }

    private fun getOrganizationsThenCredentialManifestByService() {
        val organizationDescriptor =
            if (environment == VCLEnvironment.Dev)
                Constants.OrganizationsSearchDescriptorByDidDev
            else
                Constants.OrganizationsSearchDescriptorByDidStaging
        vcl.searchForOrganizations(organizationDescriptor,
            successHandler = { organizations ->
                Log.d(TAG, "VCL Organizations received: $organizations")
//                Log.d(TAG, "VCL Organizations received")

                // choosing services[0] for testing purposes
                organizations.all.getOrNull(0)?.serviceCredentialAgentIssuers?.getOrNull(0)
                    ?.let { service ->
                        getCredentialManifestByService(service)
                    } ?: Log.e(TAG, "VCL Organizations error, issuing service not found")
            },
            errorHandler = { error ->
                logError("VCL Organizations search failed:", error)
            }
        )
    }

    private fun refreshCredentials() {
        val service = VCLService(
            JSONObject(
                Constants.IssuingServiceJsonStr
            )
        )
        val credentialManifestDescriptorRefresh =
            VCLCredentialManifestDescriptorRefresh(
                service = service,
                credentialIds = Constants.getCredentialIdsToRefresh(environment),
                didJwk = this.didJwk
            )
        vcl.getCredentialManifest(credentialManifestDescriptorRefresh,
            successHandler = { credentialManifest ->
                Log.d(
                    TAG,
                    "VCL Credentials refreshed, credential manifest: ${credentialManifest.jwt.payload}"
                )
            },
            errorHandler = { error ->
                logError("VCL Refresh Credentials failed:", error)
            })
    }

    private fun getCredentialManifestByService(serviceCredentialAgentIssuer: VCLService) {
        val credentialManifestDescriptorByOrganization =
            VCLCredentialManifestDescriptorByService(
                service = serviceCredentialAgentIssuer,
                issuingType = VCLIssuingType.Career,
                credentialTypes = serviceCredentialAgentIssuer.credentialTypes, // Can come from any where
                didJwk = this.didJwk
            )
        vcl.getCredentialManifest(credentialManifestDescriptorByOrganization,
            successHandler = { credentialManifest ->
                Log.d(TAG, "VCL Credential Manifest received: ${credentialManifest.jwt.payload}")
//                Log.d(TAG, "VCL Credential Manifest received")

                generateOffers(credentialManifest)
            },
            errorHandler = { error ->
                logError("VCL Credential Manifest failed:", error)
            })
    }

    private fun getCredentialManifestByDeepLink() {
        val deepLink =
            if (environment == VCLEnvironment.Dev)
                VCLDeepLink(Constants.CredentialManifestDeepLinkStrDev)
            else
                VCLDeepLink(Constants.CredentialManifestDeepLinkStrStaging)
        val credentialManifestDescriptorByDeepLink =
            VCLCredentialManifestDescriptorByDeepLink(
                deepLink = deepLink,
//                issuingType = VCLIssuingType.Identity,
                didJwk = this.didJwk
            )
        vcl.getCredentialManifest(credentialManifestDescriptorByDeepLink,
            successHandler = { credentialManifest ->
                Log.d(TAG, "VCL Credential Manifest received: ${credentialManifest.jwt.payload}")
//                Log.d(TAG, "VCL Credential Manifest received")

                generateOffers(credentialManifest)
            },
            errorHandler = { error ->
                logError("VCL Credential Manifest failed:", error)
            })
    }

    private fun generateOffers(credentialManifest: VCLCredentialManifest) {
        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = credentialManifest,
            types = Constants.CredentialTypes,
            identificationVerifiableCredentials = Constants.getIdentificationList(environment)
        )
        vcl.generateOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            successHandler = { offers ->
                Log.d(TAG, "VCL Generated Offers: ${offers.all}")
                Log.d(TAG, "VCL Generated Offers Response Code: ${offers.responseCode}")
                Log.d(TAG, "VCL Generated Offers Issuing Token: ${offers.sessionToken}")

//                Check offers invoked after the push notification is notified the app that offers are ready:
                checkForOffers(
                    credentialManifest = credentialManifest,
                    generateOffersDescriptor = generateOffersDescriptor,
                    sessionToken = offers.sessionToken
                )
            },
            errorHandler = { error ->
                logError("VCL failed to Generate Offers:", error)
            }
        )
    }

    private fun checkForOffers(
        credentialManifest: VCLCredentialManifest,
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        sessionToken: VCLToken
    ) {
        vcl.checkForOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            sessionToken = sessionToken,
            { offers ->
                Log.d(TAG, "VCL Checked Offers: ${offers.all}")
                Log.d(TAG, "VCL Checked Offers Response Code: ${offers.responseCode}")
                Log.d(TAG, "VCL Checked Offers Session Token: ${offers.sessionToken}")
                if (offers.responseCode == 200) {
                    finalizeOffers(
                        credentialManifest = credentialManifest,
                        offers = offers
                    )
                }
            },
            { error ->
                logError("VCL failed to Check Offers:", error)
            }
        )
    }

    private fun finalizeOffers(
        credentialManifest: VCLCredentialManifest,
        offers: VCLOffers
    ) {
        val approvedRejectedOfferIds = Utils.getApprovedRejectedOfferIdsMock(offers)
        val finalizeOffersDescriptor = VCLFinalizeOffersDescriptor(
            credentialManifest = credentialManifest,
            challenge = offers.challenge,
            approvedOfferIds = approvedRejectedOfferIds.first,
            rejectedOfferIds = approvedRejectedOfferIds.second
        )
        vcl.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptor,
            sessionToken = offers.sessionToken,
            successHandler = { verifiableCredentials ->
                Log.d(TAG, "VCL finalized Offers")
                Log.d(
                    TAG,
                    "VCL Passed Credentials: ${verifiableCredentials.passedCredentials.map { it.payload }}"
                )
                Log.d(
                    TAG,
                    "VCL Failed Credentials: ${verifiableCredentials.failedCredentials.map { it.payload }}"
                )
            },
            errorHandler = { error ->
                logError("VCL failed to finalize Offers:", error)
            }
        )
    }

    private fun getCredentialTypesUIFormSchema() {
        vcl.getCredentialTypesUIFormSchema(
            VCLCredentialTypesUIFormSchemaDescriptor(
                Constants.ResidentPermitV10,
                VCLCountries.CA
            ),
            { credentialTypesUIFormSchema ->
                Log.d(
                    TAG,
                    "VCL received Credential Types UI Form Schema: $credentialTypesUIFormSchema"
                )

            },
            { error ->
                logError("VCL failed to get Credential Types UI Form Schema:", error)
            }
        )
    }

    private fun getVerifiedProfile() {
        vcl.getVerifiedProfile(Constants.getVerifiedProfileDescriptor(environment),
            { verifiedProfile ->
                Log.d(TAG, "VCL Verified Profile: $verifiedProfile")
            },
            { error ->
                logError("VCL Verified Profile failed:", error)
            }
        )
    }

    private fun verifyJwt() {
        vcl.verifyJwt(
            Constants.SomeJwt, Constants.SomePublicJwk,
            successHandler = { isVerified ->
                Log.d(TAG, "VCL JWT verified: $isVerified")
            },
            errorHandler = { error ->
                logError("VCL JWT verification failed:", error)
            }
        )
    }

    private fun generateSignedJwt() {
        vcl.generateSignedJwt(
            didJwk = didJwk,
            jwtDescriptor = VCLJwtDescriptor(
                payload = Constants.SomePayload,
                iss = "iss123",
                jti = "jti123"
            ),
            successHandler = { jwt ->
                Log.d(TAG, "VCL JWT generated: ${jwt.encodedJwt}")
            },
            errorHandler = { error ->
                logError("VCL JWT generation failed:", error)
            }
        )
    }

    private fun generateDidJwk() {
        vcl.generateDidJwk(
            didJwkDescriptor = didJwkDescriptor,
            successHandler = { didJwk ->
                this.didJwk = didJwk
                Log.d(
                    TAG,
                    "VCL DID:JWK generated: \ndid: ${didJwk.did}\nkid: ${didJwk.kid}\nkeyId: ${didJwk.keyId}\npublicJwk: ${didJwk.publicJwk.valueStr}"
                )
            },
            errorHandler = { error ->
                logError("VCL DID:JWK generation failed:", error)
            }
        )
    }

    private fun logError(message: String = "", error: VCLError) {
        Log.e(TAG, "$message: ${error.toJsonObject()}")
    }
}


