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
import io.velocitycareerlabs.impl.data.utils.CredentialDidVerifierImpl
import io.velocitycareerlabs.impl.data.infrastructure.db.CacheServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.db.SecretStoreServiceImpl
import io.velocitycareerlabs.impl.jwt.VCLJwtServiceLocalImpl
import io.velocitycareerlabs.impl.data.infrastructure.network.NetworkServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceLocalImpl
import io.velocitycareerlabs.impl.data.models.*
import io.velocitycareerlabs.impl.data.repositories.*
import io.velocitycareerlabs.impl.data.usecases.*
import io.velocitycareerlabs.impl.data.utils.CredentialIssuerVerifierImpl
import io.velocitycareerlabs.api.jwt.VCLJwtService
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.data.utils.CredentialIssuerVerifierEmptyImpl
import io.velocitycareerlabs.impl.domain.models.*
import io.velocitycareerlabs.impl.domain.usecases.*
import io.velocitycareerlabs.impl.jwt.VCLJwtServiceRemoteImpl
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
                                } ?: throw VCLError(errorCode = VCLErrorCode.RemoteServicesUrlsNotFount.value)
                        }

                        VCLCryptoServiceType.Injected -> cryptoServicesDescriptor.injectedCryptoServicesDescriptor?.keyService?.let { keyService ->
                                return keyService
                        } ?: throw VCLError(errorCode = VCLErrorCode.InjectedServicesNotFount.value)
                }
        }
        @Throws(VCLError::class)
        internal fun chooseJwtService(
                context: Context,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): VCLJwtService {
                when (cryptoServicesDescriptor.cryptoServiceType) {
                        VCLCryptoServiceType.Local -> return VCLJwtServiceLocalImpl(
                                chooseKeyService(
                                        context,
                                        cryptoServicesDescriptor
                                )
                        )

                        VCLCryptoServiceType.Remote -> cryptoServicesDescriptor.remoteCryptoServicesUrlsDescriptor?.jwtServiceUrls?.let { jwtServiceUrls ->
                                return VCLJwtServiceRemoteImpl(
                                        NetworkServiceImpl(),
                                        jwtServiceUrls
                                )
                        } ?: throw VCLError(errorCode = VCLErrorCode.RemoteServicesUrlsNotFount.value)

                        VCLCryptoServiceType.Injected -> cryptoServicesDescriptor.injectedCryptoServicesDescriptor?.jwtService?.let { jwtService ->
                                return jwtService
                        } ?: throw VCLError(errorCode = VCLErrorCode.InjectedServicesNotFount.value)
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
                                chooseJwtService(context, cryptoServicesDescriptor)
                        ),
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
                                chooseJwtService(context, cryptoServicesDescriptor)
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
                                chooseJwtService(context, cryptoServicesDescriptor)
                        ),
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
                                chooseJwtService(context, cryptoServicesDescriptor)
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
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun provideFinalizeOffersUseCase(
                context: Context,
                credentialTypesModel: CredentialTypesModel,
                cryptoServicesDescriptor: VCLCryptoServicesDescriptor
        ): FinalizeOffersUseCase =
                FinalizeOffersUseCaseImpl(
                        FinalizeOffersRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, cryptoServicesDescriptor)
                        ),
//                        CredentialIssuerVerifierImpl(
//                                credentialTypesModel,
//                                NetworkServiceImpl()
//                        ),
                        CredentialIssuerVerifierEmptyImpl(),
                        CredentialDidVerifierImpl(),
                        ExecutorImpl()
                )

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
                                chooseJwtService(context, cryptoServicesDescriptor)
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