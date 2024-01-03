/**
 * Created by Michael Avoyan on 3/14/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl

import android.content.Context
import io.velocitycareerlabs.api.VCLCryptoServiceType
import io.velocitycareerlabs.api.entities.VCLCredentialTypes
import io.velocitycareerlabs.api.entities.error.VCLError
import io.velocitycareerlabs.api.entities.error.VCLErrorCode
import io.velocitycareerlabs.api.entities.initialization.VCLCryptoServicesDescriptor
import io.velocitycareerlabs.impl.data.verifiers.CredentialDidVerifierImpl
import io.velocitycareerlabs.impl.data.infrastructure.db.CacheServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.db.SecretStoreServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.network.NetworkServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.models.*
import io.velocitycareerlabs.impl.data.repositories.*
import io.velocitycareerlabs.impl.data.usecases.*
import io.velocitycareerlabs.api.jwt.VCLJwtSignService
import io.velocitycareerlabs.api.jwt.VCLJwtVerifyService
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.data.verifiers.CredentialIssuerVerifierEmptyImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialIssuerVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialManifestByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialsByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.OffersByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.PresentationRequestByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.domain.models.*
import io.velocitycareerlabs.impl.domain.usecases.*
import io.velocitycareerlabs.impl.domain.verifiers.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.domain.verifiers.PresentationRequestByDeepLinkVerifier
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.remote.VCLJwtSignServiceRemoteImpl
import io.velocitycareerlabs.impl.jwt.remote.VCLJwtVerifyServiceRemoteImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceRemoteImpl

internal object VclBlocksProvider {
        @Throws(VCLError::class)
        internal fun chooseKeyService(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): VCLKeyService {
                when (cryptoServicesDescriptor.cryptoServiceType) {
                        VCLCryptoServiceType.Local -> return VCLKeyServiceLocalImpl(
                                SecretStoreServiceImpl(
                                        context
                                )
                        )

                        VCLCryptoServiceType.Remote -> {
                                cryptoServicesDescriptor.remoteCryptoServicesUrlsDescriptor?.keyServiceUrls?.let { keyServiceUrls ->
                                        return VCLKeyServiceRemoteImpl(
                                                NetworkServiceImpl(),
                                                keyServiceUrls
                                        )
                                }
                                        ?: throw VCLError(errorCode = VCLErrorCode.RemoteServicesUrlsNotFount.value)
                        }

                        VCLCryptoServiceType.Injected -> cryptoServicesDescriptor.injectedCryptoServicesDescriptor?.keyService?.let { keyService ->
                                return keyService
                        } ?: throw VCLError(errorCode = VCLErrorCode.InjectedServicesNotFount.value)
                }
        }

        @Throws(VCLError::class)
        internal fun chooseJwtSignService(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): VCLJwtSignService {
                when (cryptoServicesDescriptor.cryptoServiceType) {
                        VCLCryptoServiceType.Local -> return VCLJwtSignServiceLocalImpl(
                                chooseKeyService(
                                        context,
                                        cryptoServicesDescriptor
                                )
                        )

                        VCLCryptoServiceType.Remote -> cryptoServicesDescriptor.remoteCryptoServicesUrlsDescriptor?.jwtServiceUrls?.jwtSignServiceUrl?.let { jwtSignServiceUrl ->
                                return VCLJwtSignServiceRemoteImpl(
                                        NetworkServiceImpl(),
                                        jwtSignServiceUrl
                                )
                        }
                                ?: throw VCLError(errorCode = VCLErrorCode.RemoteServicesUrlsNotFount.value)

                        VCLCryptoServiceType.Injected -> cryptoServicesDescriptor.injectedCryptoServicesDescriptor?.jwtSignService?.let { jwtSignService ->
                                return jwtSignService
                        } ?: throw VCLError(errorCode = VCLErrorCode.InjectedServicesNotFount.value)
                }
        }

        internal fun chooseJwtVerifyService(
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): VCLJwtVerifyService {
                when (cryptoServicesDescriptor.cryptoServiceType) {
                        VCLCryptoServiceType.Local -> return VCLJwtVerifyServiceLocalImpl()

                        VCLCryptoServiceType.Remote -> cryptoServicesDescriptor.remoteCryptoServicesUrlsDescriptor?.jwtServiceUrls?.jwtVerifyServiceUrl?.let { jwtVerifyServiceUrl ->
                                return VCLJwtVerifyServiceRemoteImpl(
                                        NetworkServiceImpl(),
                                        jwtVerifyServiceUrl
                                )
                        }
                                ?: return VCLJwtVerifyServiceLocalImpl() // verification may be done locally

                        VCLCryptoServiceType.Injected -> cryptoServicesDescriptor.injectedCryptoServicesDescriptor?.jwtVerifyService?.let { jwtVerifyService ->
                                return jwtVerifyService
                        }
                                ?: return VCLJwtVerifyServiceLocalImpl() // verification may be done locally
                }
        }

        @Throws(VCLError::class)
        fun provideCredentialTypeSchemasModel(
                context: Context,
                credentialTypes: VCLCredentialTypes
        ): CredentialTypeSchemasModel =
                CredentialTypeSchemasModelImpl(
                        CredentialTypeSchemasUseCaseImpl(
                                CredentialTypeSchemaRepositoryImpl(
                                        NetworkServiceImpl(),
                                        CacheServiceImpl(context)
                                ),
                                credentialTypes,
                                ExecutorImpl()
                        )
                )

        @Throws(VCLError::class)
        fun provideCredentialTypesModel(context: Context): CredentialTypesModel =
                CredentialTypesModelImpl(
                        CredentialTypesUseCaseImpl(
                                CredentialTypesRepositoryImpl(
                                        NetworkServiceImpl(),
                                        CacheServiceImpl(context)
                                ),
                                ExecutorImpl()
                        )
                )

        @Throws(VCLError::class)
        fun provideCountriesModel(context: Context): CountriesModel =
                CountriesModelImpl(
                        CountriesUseCaseImpl(
                                CountriesRepositoryImpl(
                                        NetworkServiceImpl(),
                                        CacheServiceImpl(context)
                                ),
                                ExecutorImpl()
                        )
                )

        @Throws(VCLError::class)
        fun provideServiceTypesModel(context: Context): ServiceTypesModel =
                ServiceTypesModelImpl(
                        ServiceTypesUseCaseImpl(
                                ServiceTypesRepositoryImpl(
                                        NetworkServiceImpl(),
                                        CacheServiceImpl(context)
                                ),
                                ExecutorImpl()
                        )
                )

        @Throws(VCLError::class)
        fun providePresentationRequestUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): PresentationRequestUseCase =
                PresentationRequestUseCaseImpl(
                        PresentationRequestRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ResolveKidRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor),
                                chooseJwtVerifyService(cryptoServicesDescriptor)
                        ),
                        PresentationRequestByDeepLinkVerifierImpl(),
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun providePresentationSubmissionUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): PresentationSubmissionUseCase =
                PresentationSubmissionUseCaseImpl(
                        PresentationSubmissionRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor),
                                chooseJwtVerifyService(cryptoServicesDescriptor)
                        ),
                        ExecutorImpl()
                )

        fun provideOrganizationsUseCase(): OrganizationsUseCase =
                OrganizationsUseCaseImpl(
                        OrganizationsRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun provideCredentialManifestUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): CredentialManifestUseCase =
                CredentialManifestUseCaseImpl(
                        CredentialManifestRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ResolveKidRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor),
                                chooseJwtVerifyService(cryptoServicesDescriptor)
                        ),
                        CredentialManifestByDeepLinkVerifierImpl(),
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun provideIdentificationSubmissionUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): IdentificationSubmissionUseCase =
                IdentificationSubmissionUseCaseImpl(
                        IdentificationSubmissionRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor),
                                chooseJwtVerifyService(cryptoServicesDescriptor)
                        ),
                        ExecutorImpl()
                )

        fun provideExchangeProgressUseCase(): ExchangeProgressUseCase =
                ExchangeProgressUseCaseImpl(
                        ExchangeProgressRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ExecutorImpl()
                )

        fun provideGenerateOffersUseCase(): GenerateOffersUseCase =
                GenerateOffersUseCaseImpl(
                        GenerateOffersRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        OffersByDeepLinkVerifierImpl(),
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun provideFinalizeOffersUseCase(
                context: Context,
                credentialTypesModel: CredentialTypesModel,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                isDirectIssuerCheckOn: Boolean
        ): FinalizeOffersUseCase {
                var credentialIssuerVerifier: CredentialIssuerVerifier =
                        CredentialIssuerVerifierEmptyImpl()
                if (isDirectIssuerCheckOn) {
                        credentialIssuerVerifier = CredentialIssuerVerifierImpl(
                                credentialTypesModel,
                                NetworkServiceImpl()
                        )
                }
                return FinalizeOffersUseCaseImpl(
                        FinalizeOffersRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor),
                                chooseJwtVerifyService(cryptoServicesDescriptor)
                        ),
                        credentialIssuerVerifier,
                        CredentialDidVerifierImpl(),
                        CredentialsByDeepLinkVerifierImpl(),
                        ExecutorImpl()
                )
        }

        fun provideCredentialTypesUIFormSchemaUseCase(): CredentialTypesUIFormSchemaUseCase =
                CredentialTypesUIFormSchemaUseCaseImpl(
                        CredentialTypesUIFormSchemaRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ExecutorImpl()
                )

        fun provideVerifiedProfileUseCase(): VerifiedProfileUseCase =
                VerifiedProfileUseCaseImpl(
                        VerifiedProfileRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun provideJwtServiceUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): JwtServiceUseCase =
                JwtServiceUseCaseImpl(
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor),
                                chooseJwtVerifyService(cryptoServicesDescriptor)
                        ),
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun provideKeyServiceUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): KeyServiceUseCase =
                KeyServiceUseCaseImpl(
                        KeyServiceRepositoryImpl(
                                chooseKeyService(context, cryptoServicesDescriptor)
                        ),
                        ExecutorImpl()
                )
}