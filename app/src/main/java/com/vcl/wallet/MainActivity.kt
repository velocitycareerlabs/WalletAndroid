/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vcl.wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.vcl.wallet.databinding.ActivityMainBinding
import io.velocitycareerlabs.api.VCLEnvironment
import io.velocitycareerlabs.api.VCLProvider
import io.velocitycareerlabs.api.entities.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    private lateinit var binding: ActivityMainBinding

    private val environment = VCLEnvironment.DEV
    private val vcl = VCLProvider.vclInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        vcl.initialize(
            initializationDescriptor = VCLInitializationDescriptor(
                context = this.applicationContext,
                environment = environment
            ),
            successHandler = {
                Log.d(TAG, "VCL initialization succeed!")

                showControls()
            },
            errorHandler = { error ->
                Log.e(TAG, "VCL initialization failed: $error")

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
            if (environment == VCLEnvironment.DEV)
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
                Log.e(TAG, "VCL Presentation request failed: $error")
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
        vcl.submitPresentation(presentationSubmission,
            { presentationSubmissionResult ->
                Log.d(TAG, "VCL Presentation submission result: $presentationSubmissionResult")
                vcl.getExchangeProgress(
                    VCLExchangeDescriptor(
                        presentationSubmission,
                        presentationSubmissionResult
                    ),
                    { submissionResult ->
                        Log.d(TAG, "VCL Presentation exchange progress $submissionResult")
                    },
                    { error ->
                        Log.e(TAG, "VCL Presentation exchange progress failed: $error")
                    })
            },
            { error ->
                Log.e(TAG, "VCL Presentation submission failed: $error")
            })
    }

    private fun getOrganizationsThenCredentialManifestByService() {
        val organizationDescriptor =
            if (environment == VCLEnvironment.DEV)
                Constants.OrganizationsSearchDescriptorByDidDev
            else
                Constants.OrganizationsSearchDescriptorByDidStaging
        vcl.searchForOrganizations(organizationDescriptor,
            { organizations ->
                Log.d(TAG, "VCL Organizations received: ${organizations.all}")
//                Log.d(TAG, "VCL Organizations received")

                // choosing services[0] for testing purposes
                organizations.all.getOrNull(0)?.serviceCredentialAgentIssuers?.getOrNull(0)?.let { service ->
                    getCredentialManifestByService(service)
                } ?: Log.e(TAG, "VCL Organizations error, issuing service not found")
            },
            { error ->
                Log.e(TAG, "VCL Organizations search failed: $error")
            })
    }

    private fun refreshCredentials() {
        val service = VCLService(
            JSONObject(
                Constants.IssuingServiceJsonStr)
        )
        val credentialManifestDescriptorRefresh =
            VCLCredentialManifestDescriptorRefresh(
                service = service,
                credentialIds = Constants.CredentialIds
            )
        vcl.getCredentialManifest(credentialManifestDescriptorRefresh,
            { credentialManifest ->
                Log.d(TAG, "VCL Credentials refreshed, credential manifest: ${credentialManifest.jwt.payload}")
            },
            { error ->
                Log.e(TAG, "VCL Refresh Credentials failed: $error")
            })
    }

    private fun getCredentialManifestByService(serviceCredentialAgentIssuer: VCLServiceCredentialAgentIssuer) {
        val credentialManifestDescriptorByOrganization =
            VCLCredentialManifestDescriptorByService(
                service = serviceCredentialAgentIssuer,
                credentialTypes = serviceCredentialAgentIssuer.credentialTypes // Can come from any where
            )
        vcl.getCredentialManifest(credentialManifestDescriptorByOrganization,
            { credentialManifest ->
                Log.d(TAG, "VCL Credential Manifest received: ${credentialManifest.jwt.payload}")
//                Log.d(TAG, "VCL Credential Manifest received")

                generateOffers(credentialManifest)
            },
            { error ->
                Log.e(TAG, "VCL Credential Manifest failed: $error")
            })
    }

    private fun getCredentialManifestByDeepLink() {
        val deepLink =
            if (environment == VCLEnvironment.DEV)
                VCLDeepLink(Constants.CredentialManifestDeepLinkStrDev)
            else
                VCLDeepLink(Constants.CredentialManifestDeepLinkStrStaging)
        val credentialManifestDescriptorByDeepLink =
            VCLCredentialManifestDescriptorByDeepLink(
                deepLink = deepLink
            )
        vcl.getCredentialManifest(credentialManifestDescriptorByDeepLink,
            { credentialManifest ->
                Log.d(TAG, "VCL Credential Manifest received: ${credentialManifest.jwt.payload}")
//                Log.d(TAG, "VCL Credential Manifest received")

                generateOffers(credentialManifest)
            },
            { error ->
                Log.e(TAG, "VCL Credential Manifest failed: $error")
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
                Log.e(TAG, "VCL failed to Generate Offers: $error")
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
                Log.e(TAG, "VCL failed to Check Offers: $error")
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
            approvedOfferIds = approvedRejectedOfferIds.first,
            rejectedOfferIds = approvedRejectedOfferIds.second
        )
        vcl.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptor,
            token = offers.token,
            { verifiableCredentials ->
                Log.d(TAG, "VCL finalized Offers: ${verifiableCredentials.all.map { it.payload }}")
//                Log.d(TAG, "VCL finalized Offers")
            },
            { error ->
                Log.e(TAG, "VCL failed to finalize Offers: $error")
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
                    TAG, "VCL received Credential Types UI Form Schema:" +
                            " ${credentialTypesUIFormSchema.payload}"
                )
            },
            { error ->
                Log.e(TAG, "VCL failed to get Credential Types UI Form Schema: $error")
            }
        )
    }

    private fun getVerifiedProfile() {
        vcl.getVerifiedProfile(Constants.VerifiedProfileDescriptor,
            { verifiedProfile ->
                Log.d(TAG, "VCL Verified Profile: ${verifiedProfile.credentialSubject}")
            },
            { error ->
                Log.e(TAG, "VCL Verified Profile failed: $error")
            }
        )
    }

    private fun verifyJwt() {
        vcl.verifyJwt(
            Constants.SomeJwt, Constants.SomePublicKey, { isVerified ->
                Log.d(TAG, "VCL JWT verified: $isVerified")
            },
            { error ->
                Log.e(TAG, "VCL JWT verification failed: $error")
            }
        )
    }

    private fun generateSignedJwt() {
        vcl.generateSignedJwt(Constants.SomeJson, "iss123", "jti123", { jwt ->
                Log.d(TAG, "VCL JWT generated: ${jwt.signedJwt.serialize()}")
            },
            { error ->
                Log.e(TAG, "VCL JWT generation failed: $error")
            }
        )
    }
}


