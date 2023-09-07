/**
 * Created by Michael Avoyan on 18/07/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vcl.wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.vcl.wallet.databinding.ActivityMainBinding
import io.velocitycareerlabs.api.VCL
import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.api.VCLProvider
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.initialization.VCLInitializationDescriptor
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    private lateinit var binding: ActivityMainBinding

    private val environment = VCLEnvironment.Dev
    private lateinit var vcl: VCL
    private var didJwk: VCLDidJwk? = null

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
        vcl.initialize(
            context = this.applicationContext,
            initializationDescriptor = VCLInitializationDescriptor(
                environment = environment//,
//                xVnfProtocolVersion = VCLXVnfProtocolVersion.XVnfProtocolVersion2
            ),
            successHandler = {
                Log.d(TAG, "VCL Initialization succeed!")
                showControls()

//                vcl.generateDidJwk(
//                    successHandler = { didJwk ->
//                        this.didJwk = didJwk
//                        Log.d(TAG, "VCL did:jwk is ${this.didJwk}")
//                        showControls()
//                    },
//                    errorHandler = { error ->
//                        logError("VCL Failed to generate did:jwk with error:", error)
//                        showError()
//                    }
//                )
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
                )
            ),
            { presentationRequest ->
                Log.d(TAG, "VCL Presentation request received: ${presentationRequest.jwt.payload}")
//                Log.d(TAG, "VCL Presentation request received")

                submitPresentation(presentationRequest)
            },
            { error ->
                logError("VCL Presentation request failed:", error)
            })
    }

    private fun submitPresentation(presentationRequest: VCLPresentationRequest) {
        val presentationSubmission = VCLPresentationSubmission(
            presentationRequest = presentationRequest,
            verifiableCredentials = Constants.PresentationSelectionsList
        )
        submitPresentation(presentationSubmission)
    }

    private fun submitPresentation(presentationSubmission: VCLPresentationSubmission) {
        vcl.submitPresentation(
            presentationSubmission = presentationSubmission,
            didJwk = didJwk,
            { presentationSubmissionResult ->
                Log.d(TAG, "VCL Presentation submission result: $presentationSubmissionResult")
                vcl.getExchangeProgress(
                    VCLExchangeDescriptor(
                        presentationSubmission,
                        presentationSubmissionResult
                    ),
                    { exchange ->
                        Log.d(TAG, "VCL Presentation exchange progress $exchange")
                    },
                    { error ->
                        logError("VCL Presentation exchange progress failed:", error)
                    })
            },
            { error ->
                logError("VCL Presentation submission failed:", error)
            })
    }

    private fun getOrganizationsThenCredentialManifestByService() {
        val organizationDescriptor =
            if (environment == VCLEnvironment.Dev)
                Constants.OrganizationsSearchDescriptorByDidDev
            else
                Constants.OrganizationsSearchDescriptorByDidStaging
        vcl.searchForOrganizations(organizationDescriptor,
            { organizations ->
                Log.d(TAG, "VCL Organizations received: $organizations")
//                Log.d(TAG, "VCL Organizations received")

                // choosing services[0] for testing purposes
                organizations.all.getOrNull(0)?.serviceCredentialAgentIssuers?.getOrNull(0)?.let { service ->
                    getCredentialManifestByService(service)
                } ?: Log.e(TAG, "VCL Organizations error, issuing service not found")
            },
            { error ->
                logError("VCL Organizations search failed:", error)
            }
        )
    }

    private fun refreshCredentials() {
        val service = VCLService(
            JSONObject(
                Constants.IssuingServiceJsonStr)
        )
        val credentialManifestDescriptorRefresh =
            VCLCredentialManifestDescriptorRefresh(
                service = service,
                credentialIds = Constants.CredentialIdsToRefresh
            )
        vcl.getCredentialManifest(credentialManifestDescriptorRefresh,
            { credentialManifest ->
                Log.d(TAG, "VCL Credentials refreshed, credential manifest: ${credentialManifest.jwt.payload}")
            },
            { error ->
                logError("VCL Refresh Credentials failed:", error)
            })
    }

    private fun getCredentialManifestByService(serviceCredentialAgentIssuer: VCLServiceCredentialAgentIssuer) {
        val credentialManifestDescriptorByOrganization =
            VCLCredentialManifestDescriptorByService(
                service = serviceCredentialAgentIssuer,
                issuingType = VCLIssuingType.Career,
                credentialTypes = serviceCredentialAgentIssuer.credentialTypes // Can come from any where
            )
        vcl.getCredentialManifest(credentialManifestDescriptorByOrganization,
            { credentialManifest ->
                Log.d(TAG, "VCL Credential Manifest received: ${credentialManifest.jwt.payload}")
//                Log.d(TAG, "VCL Credential Manifest received")

                generateOffers(credentialManifest)
            },
            { error ->
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
//                issuingType = VCLIssuingType.Identity
            )
        vcl.getCredentialManifest(credentialManifestDescriptorByDeepLink,
            { credentialManifest ->
                Log.d(TAG, "VCL Credential Manifest received: ${credentialManifest.jwt.payload}")
//                Log.d(TAG, "VCL Credential Manifest received")

                generateOffers(credentialManifest)
            },
            { error ->
                logError("VCL Credential Manifest failed:", error)
            })
    }

    private fun generateOffers(credentialManifest: VCLCredentialManifest) {
        val generateOffersDescriptor = VCLGenerateOffersDescriptor(
            credentialManifest = credentialManifest,
            types = Constants.CredentialTypes,
            identificationVerifiableCredentials = Constants.IdentificationList
        )
        vcl.generateOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            didJwk = didJwk,
            { offers ->
                Log.d(TAG, "VCL Generated Offers: ${offers.all}")
                Log.d(TAG, "VCL Generated Offers Response Code: ${offers.responseCode}")
                Log.d(TAG, "VCL Generated Offers Token: ${offers.token}")

//                Check offers invoked after the push notification is notified the app that offers are ready:
                checkForOffers(
                    credentialManifest = credentialManifest,
                    generateOffersDescriptor = generateOffersDescriptor,
                    token = offers.token
                )
            },
            { error ->
                logError("VCL failed to Generate Offers:", error)
            }
        )
    }

    private fun checkForOffers(
        credentialManifest: VCLCredentialManifest,
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        token: VCLToken
    ) {
        vcl.checkForOffers(
            generateOffersDescriptor = generateOffersDescriptor,
            token = token,
            { offers ->
                Log.d(TAG, "VCL Checked Offers: ${offers.all}")
                Log.d(TAG, "VCL Checked Offers Response Code: ${offers.responseCode}")
                Log.d(TAG, "VCL Checked Offers Token: ${offers.token}")
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
            offers = offers,
            approvedOfferIds = approvedRejectedOfferIds.first,
            rejectedOfferIds = approvedRejectedOfferIds.second
        )
        vcl.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptor,
            didJwk = didJwk,
            token = offers.token,
            { verifiableCredentials ->
                Log.d(TAG, "VCL finalized Offers")
                Log.d(TAG, "VCL Passed Credentials: ${verifiableCredentials.passedCredentials.map { it.payload }}")
                Log.d(TAG, "VCL Failed Credentials: ${verifiableCredentials.failedCredentials.map { it.payload }}")
            },
            { error ->
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
                Log.d(TAG, "VCL received Credential Types UI Form Schema: $credentialTypesUIFormSchema")

            },
            { error ->
                logError("VCL failed to get Credential Types UI Form Schema:", error)
            }
        )
    }

    private fun getVerifiedProfile() {
        vcl.getVerifiedProfile(Constants.VerifiedProfileDescriptor,
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
            Constants.SomeJwt, Constants.SomeJwkPublic, { isVerified ->
                Log.d(TAG, "VCL JWT verified: $isVerified")
            },
            { error ->
                logError("VCL JWT verification failed:", error)
            }
        )
    }

    private fun generateSignedJwt() {
        vcl.generateSignedJwt(
            VCLJwtDescriptor(
                keyId = didJwk?.keyId,
                payload = Constants.SomePayload,
                iss = "iss123",
                jti = "jti123"
            ),
            { jwt ->
                Log.d(TAG, "VCL JWT generated: ${jwt.encodedJwt}")
            },
            { error ->
                logError("VCL JWT generation failed:", error)
            }
        )
    }

    private fun generateDidJwk() {
        vcl.generateDidJwk(
            { didJwk ->
                Log.d(TAG, "VCL DID:JWK generated: ${didJwk.value}")
            },
            { error ->
                logError("VCL DID:JWK generation failed:", error)
            }
        )
    }

    private fun logError(message: String = "", error: VCLError) {
        Log.e(TAG, "$message: ${error.toJsonObject()}")
    }
}


