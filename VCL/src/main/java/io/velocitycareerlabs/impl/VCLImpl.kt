/**
 * Created by Michael Avoyan on 3/11/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl

import android.content.Context
import io.velocitycareerlabs.api.VCL
import io.velocitycareerlabs.api.entities.*
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.impl.domain.models.CredentialTypeSchemasModel
import io.velocitycareerlabs.api.entities.handleResult
import io.velocitycareerlabs.api.entities.initialization.VCLInitializationDescriptor
import io.velocitycareerlabs.impl.domain.models.CountriesModel
import io.velocitycareerlabs.impl.domain.models.CredentialTypesModel
import io.velocitycareerlabs.impl.domain.usecases.CredentialManifestUseCase
import io.velocitycareerlabs.impl.domain.usecases.CredentialTypesUIFormSchemaUseCase
import io.velocitycareerlabs.impl.domain.usecases.ExchangeProgressUseCase
import io.velocitycareerlabs.impl.domain.usecases.FinalizeOffersUseCase
import io.velocitycareerlabs.impl.domain.usecases.GenerateOffersUseCase
import io.velocitycareerlabs.impl.domain.usecases.IdentificationSubmissionUseCase
import io.velocitycareerlabs.impl.domain.usecases.JwtServiceUseCase
import io.velocitycareerlabs.impl.domain.usecases.KeyServiceUseCase
import io.velocitycareerlabs.impl.domain.usecases.OrganizationsUseCase
import io.velocitycareerlabs.impl.domain.usecases.PresentationRequestUseCase
import io.velocitycareerlabs.impl.domain.usecases.PresentationSubmissionUseCase
import io.velocitycareerlabs.impl.domain.usecases.VerifiedProfileUseCase
import io.velocitycareerlabs.impl.utils.InitializationWatcher
import io.velocitycareerlabs.impl.utils.VCLLog
import io.velocitycareerlabs.impl.utils.ProfileServiceTypeVerifier
import kotlin.jvm.Throws

internal class VCLImpl: VCL {
    companion object {
        val TAG = VCLImpl::class.simpleName

        private const val ModelsToInitializeAmount = 3
    }
    private lateinit var initializationDescriptor: VCLInitializationDescriptor

    private lateinit var credentialTypesModel: CredentialTypesModel
    private lateinit var credentialTypeSchemasModel: CredentialTypeSchemasModel
    private lateinit var countriesModel: CountriesModel

    private lateinit var presentationRequestUseCase: PresentationRequestUseCase
    private lateinit var presentationSubmissionUseCase: PresentationSubmissionUseCase
    private lateinit var exchangeProgressUseCase: ExchangeProgressUseCase
    private lateinit var organizationsUseCase: OrganizationsUseCase
    private lateinit var credentialManifestUseCase: CredentialManifestUseCase
    private lateinit var identificationSubmissionUseCase: IdentificationSubmissionUseCase
    private lateinit var generateOffersUseCase: GenerateOffersUseCase
    private lateinit var finalizeOffersUseCase: FinalizeOffersUseCase
    private lateinit var credentialTypesUIFormSchemaUseCase: CredentialTypesUIFormSchemaUseCase
    private lateinit var verifiedProfileUseCase: VerifiedProfileUseCase
    private lateinit var jwtServiceUseCase: JwtServiceUseCase
    private lateinit var keyServiceUseCase: KeyServiceUseCase

    private var initializationWatcher = InitializationWatcher(ModelsToInitializeAmount)
    private lateinit var profileServiceTypeVerifier: ProfileServiceTypeVerifier

    override fun initialize(
        context: Context,
        initializationDescriptor: VCLInitializationDescriptor,
        successHandler: () -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        this.initializationDescriptor = initializationDescriptor

        initGlobalConfigurations()

        printVersion()

        this.initializationWatcher = InitializationWatcher(ModelsToInitializeAmount)

        cacheRemoteData(
            context = context.applicationContext,
            cacheSequence = initializationDescriptor.cacheSequence,
            successHandler = successHandler,
            errorHandler = errorHandler
        )
    }

    @Throws
    private fun initializeUseCases(context: Context) {
        presentationRequestUseCase =
            VclBlocksProvider.providePresentationRequestUseCase(
                context,
                initializationDescriptor.cryptoServicesDescriptor
            )
        presentationSubmissionUseCase = VclBlocksProvider.providePresentationSubmissionUseCase(
            context,
            initializationDescriptor.cryptoServicesDescriptor
        )
        exchangeProgressUseCase = VclBlocksProvider.provideExchangeProgressUseCase()
        organizationsUseCase = VclBlocksProvider.provideOrganizationsUseCase()
        credentialManifestUseCase =
            VclBlocksProvider.provideCredentialManifestUseCase(
                context,
                initializationDescriptor.cryptoServicesDescriptor
            )
        identificationSubmissionUseCase = VclBlocksProvider.provideIdentificationSubmissionUseCase(
            context,
            initializationDescriptor.cryptoServicesDescriptor
        )
        generateOffersUseCase = VclBlocksProvider.provideGenerateOffersUseCase()
        finalizeOffersUseCase =
            VclBlocksProvider.provideFinalizeOffersUseCase(
                context,
                credentialTypesModel,
                initializationDescriptor.cryptoServicesDescriptor,
                initializationDescriptor.isDirectIssuerCheckOn
            )
        credentialTypesUIFormSchemaUseCase =
            VclBlocksProvider.provideCredentialTypesUIFormSchemaUseCase()
        verifiedProfileUseCase = VclBlocksProvider.provideVerifiedProfileUseCase()
        jwtServiceUseCase =
            VclBlocksProvider.provideJwtServiceUseCase(
                context,
                initializationDescriptor.cryptoServicesDescriptor
            )
        keyServiceUseCase =
            VclBlocksProvider.provideKeyServiceUseCase(
                context,
                initializationDescriptor.cryptoServicesDescriptor
            )
    }

    private fun completionHandler(
        context: Context,
        successHandler: () -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        initializationWatcher.firstError()?.let { errorHandler(it) }
            ?: run {
                try {
                    initializeUseCases(context)

                    profileServiceTypeVerifier = ProfileServiceTypeVerifier(verifiedProfileUseCase)

                    successHandler()

                } catch (error: VCLError) {
                    errorHandler(error)
                } catch (ex: Exception) {
                    errorHandler(VCLError(exception = ex))
                }
            }
    }

    private fun cacheRemoteData(
        context: Context,
        cacheSequence: Int,
        successHandler: () -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        credentialTypesModel =
            VclBlocksProvider.provideCredentialTypesModel(context)
        countriesModel =
            VclBlocksProvider.provideCountriesModel(context)

        countriesModel.initialize(cacheSequence) { result ->
            result.handleResult(
                {
                    if (initializationWatcher.onInitializedModel(null))
                        completionHandler(context, successHandler, errorHandler)
                },
                { error ->
                    if (initializationWatcher.onInitializedModel(error))
                        completionHandler(context, successHandler, errorHandler)
                })
        }
        credentialTypesModel.initialize(cacheSequence) { result ->
            result.handleResult(
                {
                    if (initializationWatcher.onInitializedModel(null)) {
                        completionHandler(context, successHandler, errorHandler)
                    } else {
                        credentialTypesModel.data?.let { credentialTypes ->
                            credentialTypeSchemasModel =
                                VclBlocksProvider.provideCredentialTypeSchemasModel(
                                    context,
                                    credentialTypes
                                )
                            credentialTypeSchemasModel.initialize(cacheSequence) { result ->
                                result.handleResult(
                                    {
                                        if (initializationWatcher.onInitializedModel(null)) {
                                            completionHandler(context, successHandler, errorHandler)
                                        }
                                    },
                                    { error ->
                                        if (initializationWatcher.onInitializedModel(error)) {
                                            completionHandler(context, successHandler, errorHandler)
                                        }
                                    }
                                )
                            }
                        } ?: run {
                            errorHandler(VCLError("Failed to get credential type schemas"))
                        }
                    }
                },
                { error ->
                    if (initializationWatcher.onInitializedModel(error, true)) {
                        completionHandler(context, successHandler, errorHandler)
                    }
                })
        }
    }

    private fun initGlobalConfigurations() {
        GlobalConfig.CurrentEnvironment = initializationDescriptor.environment
        GlobalConfig.XVnfProtocolVersion = initializationDescriptor.xVnfProtocolVersion
        GlobalConfig.IsDebugOn = initializationDescriptor.isDebugOn
    }

    override val countries: VCLCountries? get() = countriesModel.data
    override val credentialTypes: VCLCredentialTypes? get() = credentialTypesModel.data
    override val credentialTypeSchemas: VCLCredentialTypeSchemas? get() = credentialTypeSchemasModel.data

    override fun getPresentationRequest(
        presentationRequestDescriptor: VCLPresentationRequestDescriptor,
        successHandler: (VCLPresentationRequest) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        presentationRequestDescriptor.did?.let { did ->
            profileServiceTypeVerifier.verifyServiceTypeOfVerifiedProfile(
                verifiedProfileDescriptor = VCLVerifiedProfileDescriptor(did = did),
                expectedServiceTypes = VCLServiceTypes(VCLServiceType.Inspector),
                successHandler = { _ ->
                    presentationRequestUseCase.getPresentationRequest(
                        presentationRequestDescriptor
                    ) { presentationRequestResult ->
                        presentationRequestResult.handleResult(
                            {
                                successHandler(it)
                            },
                            {
                                logError("getPresentationRequest", it)
                                errorHandler(it)
                            }
                        )
                    }
                },
                errorHandler = {
                    logError("profile verification failed", it)
                    errorHandler(it)
                }
            )
        } ?: run {
            VCLError("did was not found in $presentationRequestDescriptor").let {
                logError("getPresentationRequest::verifiedProfile", it)
                errorHandler(it)
            }
        }
    }

    override fun submitPresentation(
        presentationSubmission: VCLPresentationSubmission,
        successHandler: (VCLSubmissionResult) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        presentationSubmissionUseCase.submit(
            submission = presentationSubmission
        ) { presentationSubmissionResult ->
            presentationSubmissionResult.handleResult(
                {
                    successHandler(it)
                },
                {
                    logError("submit presentation", it)
                    errorHandler(it)
                }
            )
        }
    }

    override fun getExchangeProgress(
        exchangeDescriptor: VCLExchangeDescriptor,
        successHandler: (VCLExchange) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        exchangeProgressUseCase.getExchangeProgress(exchangeDescriptor) { presentationSubmissionResult ->
            presentationSubmissionResult.handleResult(
                {
                    successHandler(it)
                },
                {
                    logError("getExchangeProgress", it)
                    errorHandler(it)
                }
            )
        }
    }

    override fun searchForOrganizations(
        organizationsSearchDescriptor: VCLOrganizationsSearchDescriptor,
        successHandler: (VCLOrganizations) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        organizationsUseCase.searchForOrganizations(organizationsSearchDescriptor) { organization ->
            organization.handleResult(
                {
                    successHandler(it)
                },
                {
                    logError("searchForOrganizations", it)
                    errorHandler(it)
                }
            )
        }
    }

    override fun getCredentialManifest(
        credentialManifestDescriptor: VCLCredentialManifestDescriptor,
        successHandler: (VCLCredentialManifest) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        VCLLog.d(TAG, "credentialManifestDescriptor: ${credentialManifestDescriptor.toPropsString()}")
        credentialManifestDescriptor.did?.let { did ->
            profileServiceTypeVerifier.verifyServiceTypeOfVerifiedProfile(
                verifiedProfileDescriptor = VCLVerifiedProfileDescriptor(did = did),
                expectedServiceTypes = VCLServiceTypes(credentialManifestDescriptor.issuingType),
                successHandler = { verifiedProfile ->
                    credentialManifestUseCase.getCredentialManifest(
                        credentialManifestDescriptor,
                        verifiedProfile
                    ) { credentialManifest ->
                        credentialManifest.handleResult(
                            {
                                successHandler(it)
                            },
                            {
                                logError("getCredentialManifest", it)
                                errorHandler(it)
                            }
                        )
                    }
                },
                errorHandler = {
                    logError("profile verification failed", it)
                    errorHandler(it)
                }
            )
        } ?: run {
            VCLError("did was not found in $credentialManifestDescriptor").let {
                logError("getCredentialManifest::verifiedProfile", it)
                errorHandler(it)
            }
        }
    }

    override fun generateOffers(
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        successHandler: (VCLOffers) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        val identificationSubmission = VCLIdentificationSubmission(
            credentialManifest = generateOffersDescriptor.credentialManifest,
            verifiableCredentials = generateOffersDescriptor.identificationVerifiableCredentials
        )
        identificationSubmissionUseCase.submit(
            submission = identificationSubmission
        ) { identificationSubmissionResult ->
            identificationSubmissionResult.handleResult(
                { identificationSubmission ->
                    invokeGenerateOffersUseCase(
                        generateOffersDescriptor = generateOffersDescriptor,
                        sessionToken = identificationSubmission.sessionToken,
                        successHandler = successHandler,
                        errorHandler = errorHandler
                    )
                },
                {
                    logError("submit identification", it)
                    errorHandler(it)
                }
            )
        }
    }

    override fun checkForOffers(
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        sessionToken: VCLToken,
        successHandler: (VCLOffers) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        invokeGenerateOffersUseCase(
            generateOffersDescriptor = generateOffersDescriptor,
            sessionToken = sessionToken,
            successHandler = successHandler,
            errorHandler = errorHandler
        )
    }

    private fun invokeGenerateOffersUseCase(
        generateOffersDescriptor: VCLGenerateOffersDescriptor,
        sessionToken: VCLToken,
        successHandler: (VCLOffers) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        generateOffersUseCase.generateOffers(
            generateOffersDescriptor,
            sessionToken
        ) { vnOffersResult ->
            vnOffersResult.handleResult(
                {
                    successHandler(it)
                },
                {
                    logError("generateOffers", it)
                    errorHandler(it)
                }
            )
        }
    }

    override fun finalizeOffers(
        finalizeOffersDescriptor: VCLFinalizeOffersDescriptor,
        sessionToken: VCLToken,
        successHandler: (VCLJwtVerifiableCredentials) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        finalizeOffersUseCase.finalizeOffers(
            finalizeOffersDescriptor = finalizeOffersDescriptor,
            sessionToken = sessionToken
        ) { jwtVerifiableCredentials ->
            jwtVerifiableCredentials.handleResult(
                {
                    successHandler(it)
                },
                {
                    logError("finalizeOffers", it)
                    errorHandler(it)
                }
            )
        }
    }

    override fun getCredentialTypesUIFormSchema(
        credentialTypesUIFormSchemaDescriptor: VCLCredentialTypesUIFormSchemaDescriptor,
        successHandler: (VCLCredentialTypesUIFormSchema) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        countriesModel.data?.let { countries ->
            credentialTypesUIFormSchemaUseCase.getCredentialTypesUIFormSchema(
                credentialTypesUIFormSchemaDescriptor,
                countries
            ) { credentialTypesUIFormSchemaResult ->
                credentialTypesUIFormSchemaResult.handleResult(
                    {
                        successHandler(it)
                    },
                    {
                        logError("getCredentialTypesUIFormSchema", it)
                        errorHandler(it)
                    }
                )
            }
        } ?: run {
            val error = VCLError("No countries for getCredentialTypesUIFormSchema")
            logError("getCredentialTypesUIFormSchema", error)
            errorHandler(error)
        }
    }

    override fun getVerifiedProfile(
        verifiedProfileDescriptor: VCLVerifiedProfileDescriptor,
        successHandler: (VCLVerifiedProfile) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        verifiedProfileUseCase.getVerifiedProfile(verifiedProfileDescriptor) { verifiedProfileResult ->
            verifiedProfileResult.handleResult(
                {
                    successHandler(it)
                },
                {
                    logError("getVerifiedProfile", it)
                    errorHandler(it)
                }
            )
        }
    }

    override fun verifyJwt(
        jwt: VCLJwt,
        publicJwk: VCLPublicJwk,
        remoteCryptoServicesToken: VCLToken?,
        successHandler: (Boolean) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        jwtServiceUseCase.verifyJwt(
            jwt = jwt,
            publicJwk = publicJwk,
            remoteCryptoServicesToken = remoteCryptoServicesToken
        ) { isVerifiedResult ->
            isVerifiedResult.handleResult(
                {
                    successHandler(it)
                },
                {
                    logError("verifyJwt", it)
                    errorHandler(it)
                }
            )
        }
    }

    override fun generateSignedJwt(
        jwtDescriptor: VCLJwtDescriptor,
        didJwk: VCLDidJwk,
        remoteCryptoServicesToken: VCLToken?,
        successHandler: (VCLJwt) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        jwtServiceUseCase.generateSignedJwt(
            jwtDescriptor = jwtDescriptor,
            didJwk = didJwk,
            remoteCryptoServicesToken = remoteCryptoServicesToken
        ) { jwtResult ->
            jwtResult.handleResult(
                {
                    successHandler(it)
                },
                {
                    logError("generateSignedJwt", it)
                    errorHandler(it)
                }
            )
        }
    }

    override fun generateDidJwk(
        remoteCryptoServicesToken: VCLToken?,
        successHandler: (VCLDidJwk) -> Unit,
        errorHandler: (VCLError) -> Unit
    ) {
        keyServiceUseCase.generateDidJwk(remoteCryptoServicesToken) { didJwkResult ->
            didJwkResult.handleResult(
                {
                    successHandler(it)
                },
                {
                    logError("generateDidJwk", it)
                    errorHandler(it)
                }
            )
        }
    }
}

internal fun VCLImpl.logError(message: String = "", error: VCLError) {
//    VCLLog.e(VCLImpl.TAG, "error.payload: ${error.payload}, error.message: ${error.message}, error.error: ${error.error}, error.errorCode: ${error.errorCode}, error.statusCode: ${error.statusCode}")
    VCLLog.e(VCLImpl.TAG, "$message: ${error.toJsonObject()}")
}

internal fun VCLImpl.printVersion() {
    VCLLog.d("VCL", "Version: ${GlobalConfig.VersionName}")
    VCLLog.d("VCL", "Build: ${GlobalConfig.VersionCode}")
}