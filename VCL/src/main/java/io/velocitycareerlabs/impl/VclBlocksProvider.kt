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
import io.velocitycareerlabs.impl.data.infrastructure.network.Request
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.models.*
import io.velocitycareerlabs.impl.data.repositories.*
import io.velocitycareerlabs.impl.data.usecases.*
import io.velocitycareerlabs.api.jwt.VCLJwtSignService
import io.velocitycareerlabs.api.jwt.VCLJwtVerifyService
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.data.verifiers.directissuerverification.CredentialIssuerVerifierEmptyImpl
import io.velocitycareerlabs.impl.data.verifiers.directissuerverification.CredentialIssuerVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialManifestByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.CredentialsByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.OffersByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.PresentationRequestByDeepLinkVerifierImpl
import io.velocitycareerlabs.impl.data.verifiers.directissuerverification.repositories.CredentialSubjectContextRepositoryImpl
import io.velocitycareerlabs.impl.domain.models.*
import io.velocitycareerlabs.impl.domain.usecases.*
import io.velocitycareerlabs.impl.domain.verifiers.CredentialIssuerVerifier
import io.velocitycareerlabs.impl.jwt.local.VCLJwtSignServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.local.VCLJwtVerifyServiceLocalImpl
import io.velocitycareerlabs.impl.jwt.remote.VCLJwtSignServiceRemoteImpl
import io.velocitycareerlabs.impl.jwt.remote.VCLJwtVerifyServiceRemoteImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceRemoteImpl
import java.net.HttpURLConnection

internal object VclBlocksProvider {
        private fun networkService(
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ) = NetworkServiceImpl(connectionFactory)

        @Throws(VCLError::class)
        internal fun chooseKeyService(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
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
                                                networkService(connectionFactory),
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
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): VCLJwtSignService {
                when (cryptoServicesDescriptor.cryptoServiceType) {
                        VCLCryptoServiceType.Local -> return VCLJwtSignServiceLocalImpl(
                                chooseKeyService(
                                        context,
                                        cryptoServicesDescriptor,
                                        connectionFactory
                                )
                        )

                        VCLCryptoServiceType.Remote -> cryptoServicesDescriptor.remoteCryptoServicesUrlsDescriptor?.jwtServiceUrls?.jwtSignServiceUrl?.let { jwtSignServiceUrl ->
                                return VCLJwtSignServiceRemoteImpl(
                                        networkService(connectionFactory),
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
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): VCLJwtVerifyService {
                when (cryptoServicesDescriptor.cryptoServiceType) {
                        VCLCryptoServiceType.Local -> return VCLJwtVerifyServiceLocalImpl()

                        VCLCryptoServiceType.Remote -> cryptoServicesDescriptor.remoteCryptoServicesUrlsDescriptor?.jwtServiceUrls?.jwtVerifyServiceUrl?.let { jwtVerifyServiceUrl ->
                                return VCLJwtVerifyServiceRemoteImpl(
                                        networkService(connectionFactory),
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
                credentialTypes: VCLCredentialTypes,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): CredentialTypeSchemasModel =
                CredentialTypeSchemasModelImpl(
                        CredentialTypeSchemasUseCaseImpl(
                                CredentialTypeSchemaRepositoryImpl(
                                        networkService(connectionFactory),
                                        CacheServiceImpl(context)
                                ),
                                credentialTypes,
                                ExecutorImpl.instance
                        )
                )

        @Throws(VCLError::class)
        fun provideCredentialTypesModel(
                context: Context,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): CredentialTypesModel =
                CredentialTypesModelImpl(
                        CredentialTypesUseCaseImpl(
                                CredentialTypesRepositoryImpl(
                                        networkService(connectionFactory),
                                        CacheServiceImpl(context)
                                ),
                                ExecutorImpl.instance
                        )
                )

        @Throws(VCLError::class)
        fun provideCountriesModel(
                context: Context,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): CountriesModel =
                CountriesModelImpl(
                        CountriesUseCaseImpl(
                                CountriesRepositoryImpl(
                                        networkService(connectionFactory),
                                        CacheServiceImpl(context)
                                ),
                                ExecutorImpl.instance
                        )
                )

        @Throws(VCLError::class)
        fun providePresentationRequestUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): PresentationRequestUseCase =
                PresentationRequestUseCaseImpl(
                        PresentationRequestRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        ResolveDidDocumentRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor, connectionFactory),
                                chooseJwtVerifyService(cryptoServicesDescriptor, connectionFactory)
                        ),
                        PresentationRequestByDeepLinkVerifierImpl(),
                        ExecutorImpl.instance
                )

        @Throws(VCLError::class)
        fun providePresentationSubmissionUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): PresentationSubmissionUseCase =
                PresentationSubmissionUseCaseImpl(
                        PresentationSubmissionRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor, connectionFactory),
                                chooseJwtVerifyService(cryptoServicesDescriptor, connectionFactory)
                        ),
                        ExecutorImpl.instance
                )

        fun provideOrganizationsUseCase(
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): OrganizationsUseCase =
                OrganizationsUseCaseImpl(
                        OrganizationsRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        ExecutorImpl.instance
                )

        @Throws(VCLError::class)
        fun provideCredentialManifestUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): CredentialManifestUseCase =
                CredentialManifestUseCaseImpl(
                        CredentialManifestRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        ResolveDidDocumentRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor, connectionFactory),
                                chooseJwtVerifyService(cryptoServicesDescriptor, connectionFactory)
                        ),
                        CredentialManifestByDeepLinkVerifierImpl(),
                        ExecutorImpl.instance
                )

        @Throws(VCLError::class)
        fun provideIdentificationSubmissionUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): IdentificationSubmissionUseCase =
                IdentificationSubmissionUseCaseImpl(
                        IdentificationSubmissionRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor, connectionFactory),
                                chooseJwtVerifyService(cryptoServicesDescriptor, connectionFactory)
                        ),
                        ExecutorImpl.instance
                )

        fun provideExchangeProgressUseCase(
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): ExchangeProgressUseCase =
                ExchangeProgressUseCaseImpl(
                        ExchangeProgressRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        ExecutorImpl.instance
                )

        fun provideGenerateOffersUseCase(
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): GenerateOffersUseCase =
                GenerateOffersUseCaseImpl(
                        GenerateOffersRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        OffersByDeepLinkVerifierImpl(
                                ResolveDidDocumentRepositoryImpl(
                                        networkService(connectionFactory)
                                )
                        ),
                        ExecutorImpl.instance
                )

        @Throws(VCLError::class)
        fun provideFinalizeOffersUseCase(
                context: Context,
                credentialTypesModel: CredentialTypesModel,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                isDirectIssuerCheckOn: Boolean,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): FinalizeOffersUseCase {
                var credentialIssuerVerifier: CredentialIssuerVerifier =
                        CredentialIssuerVerifierEmptyImpl()
                if (isDirectIssuerCheckOn) {
                        credentialIssuerVerifier = CredentialIssuerVerifierImpl(
                                credentialTypesModel,
                                CredentialSubjectContextRepositoryImpl(
                                        networkService(connectionFactory)
                                )
                        )
                }
                return FinalizeOffersUseCaseImpl(
                        FinalizeOffersRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor, connectionFactory),
                                chooseJwtVerifyService(cryptoServicesDescriptor, connectionFactory)
                        ),
                        credentialIssuerVerifier,
                        CredentialDidVerifierImpl(),
                        CredentialsByDeepLinkVerifierImpl(
                                ResolveDidDocumentRepositoryImpl(
                                        networkService(connectionFactory)
                                )
                        ),
                        ExecutorImpl.instance
                )
        }

        @Throws(VCLError::class)
        fun provideAuthTokenUseCase(
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): AuthTokenUseCase =
                AuthTokenUseCaseImpl(
                        AuthTokenRepositoryImpl(networkService(connectionFactory)),
                        ExecutorImpl.instance
                )

        fun provideCredentialTypesUIFormSchemaUseCase(
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): CredentialTypesUIFormSchemaUseCase =
                CredentialTypesUIFormSchemaUseCaseImpl(
                        CredentialTypesUIFormSchemaRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        ExecutorImpl.instance
                )

        fun provideVerifiedProfileUseCase(
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): VerifiedProfileUseCase =
                VerifiedProfileUseCaseImpl(
                        VerifiedProfileRepositoryImpl(
                                networkService(connectionFactory)
                        ),
                        ExecutorImpl.instance
                )

        @Throws(VCLError::class)
        fun provideJwtServiceUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): JwtServiceUseCase =
                JwtServiceUseCaseImpl(
                        JwtServiceRepositoryImpl(
                                chooseJwtSignService(context, cryptoServicesDescriptor, connectionFactory),
                                chooseJwtVerifyService(cryptoServicesDescriptor, connectionFactory)
                        ),
                        ExecutorImpl.instance
                )

        @Throws(VCLError::class)
        fun provideKeyServiceUseCase(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor,
                connectionFactory: ((Request) -> HttpURLConnection)? = null
        ): KeyServiceUseCase =
                KeyServiceUseCaseImpl(
                        KeyServiceRepositoryImpl(
                                chooseKeyService(context, cryptoServicesDescriptor, connectionFactory)
                        ),
                        ExecutorImpl.instance
                )
}
