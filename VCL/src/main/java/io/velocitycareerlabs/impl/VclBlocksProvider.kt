/**
 * Created by Michael Avoyan on 3/14/21.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl

import android.content.Context
import io.velocitycareerlabs.api.VCLKeyServiceType
import io.velocitycareerlabs.api.entities.VCLCredentialTypes
import io.velocitycareerlabs.api.entities.VCLError
import io.velocitycareerlabs.api.entities.VCLErrorCode
import io.velocitycareerlabs.api.entities.initialization.VCLKeyServicesDescriptor
import io.velocitycareerlabs.impl.data.utils.CredentialDidVerifierImpl
import io.velocitycareerlabs.impl.data.infrastructure.db.CacheServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.db.SecretStoreServiceImpl
import io.velocitycareerlabs.impl.jwt.VCLJwtServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.network.NetworkServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceImpl
import io.velocitycareerlabs.impl.data.models.*
import io.velocitycareerlabs.impl.data.repositories.*
import io.velocitycareerlabs.impl.data.usecases.*
import io.velocitycareerlabs.impl.data.utils.CredentialIssuerVerifierImpl
import io.velocitycareerlabs.api.jwt.VCLJwtService
import io.velocitycareerlabs.api.keys.VCLKeyService
import io.velocitycareerlabs.impl.domain.models.*
import io.velocitycareerlabs.impl.domain.usecases.*
import io.velocitycareerlabs.impl.jwt.VCLJwtRemoteServiceImpl
import io.velocitycareerlabs.impl.keys.VCLKeyServiceRemoteImpl

internal object VclBlocksProvider {
        @Throws(VCLError::class)
        internal fun chooseKeyService(
                context: Context,
                keyServicesDescriptor: VCLKeyServicesDescriptor
        ): VCLKeyService {
                when (keyServicesDescriptor.keyServiceType) {
                        VCLKeyServiceType.Local -> return VCLKeyServiceImpl(
                                SecretStoreServiceImpl(
                                        context
                                )
                        )

                        VCLKeyServiceType.Remote -> {
                                keyServicesDescriptor.remoteServicesUrlsDescriptor?.keyServiceUrls?.let { keyServiceUrls ->
                                        return VCLKeyServiceRemoteImpl(
                                                NetworkServiceImpl(),
                                                keyServiceUrls
                                        )
                                } ?: throw VCLError(errorCode = VCLErrorCode.RemoteServicesUrlsNotFount.value)
                        }

                        VCLKeyServiceType.Injected -> keyServicesDescriptor.injectedServicesDescriptor?.keyService?.let { keyService ->
                                return keyService
                        } ?: throw VCLError(errorCode = VCLErrorCode.InjectedServicesNotFount.value)
                }
        }
        @Throws(VCLError::class)
        internal fun chooseJwtService(
                context: Context,
                keyServicesDescriptor: VCLKeyServicesDescriptor
        ): VCLJwtService {
                when (keyServicesDescriptor.keyServiceType) {
                        VCLKeyServiceType.Local -> return VCLJwtServiceImpl(
                                chooseKeyService(
                                        context,
                                        keyServicesDescriptor
                                )
                        )

                        VCLKeyServiceType.Remote -> keyServicesDescriptor.remoteServicesUrlsDescriptor?.jwtServiceUrls?.let { jwtServiceUrls ->
                                return VCLJwtRemoteServiceImpl(
                                        NetworkServiceImpl(),
                                        jwtServiceUrls
                                )
                        } ?: throw VCLError(errorCode = VCLErrorCode.RemoteServicesUrlsNotFount.value)

                        VCLKeyServiceType.Injected -> keyServicesDescriptor.injectedServicesDescriptor?.jwtService?.let { jwtService ->
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
                keyServicesDescriptor: VCLKeyServicesDescriptor
        ): PresentationRequestUseCase =
                PresentationRequestUseCaseImpl(
                        PresentationRequestRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ResolveKidRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServicesDescriptor)
                        ),
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun providePresentationSubmissionUseCase(
                context: Context,
                keyServicesDescriptor: VCLKeyServicesDescriptor
        ): PresentationSubmissionUseCase =
                PresentationSubmissionUseCaseImpl(
                        PresentationSubmissionRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServicesDescriptor)
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
                keyServicesDescriptor: VCLKeyServicesDescriptor
        ): CredentialManifestUseCase =
                CredentialManifestUseCaseImpl(
                        CredentialManifestRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ResolveKidRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServicesDescriptor)
                        ),
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun provideIdentificationSubmissionUseCase(
                context: Context,
                keyServicesDescriptor: VCLKeyServicesDescriptor
        ): IdentificationSubmissionUseCase =
                IdentificationSubmissionUseCaseImpl(
                        IdentificationSubmissionRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServicesDescriptor)
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
                keyServicesDescriptor: VCLKeyServicesDescriptor
        ): FinalizeOffersUseCase =
                FinalizeOffersUseCaseImpl(
                        FinalizeOffersRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServicesDescriptor)
                        ),
                        CredentialIssuerVerifierImpl(
                                credentialTypesModel,
                                NetworkServiceImpl()
                        ),
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
                keyServicesDescriptor: VCLKeyServicesDescriptor
        ): JwtServiceUseCase =
                JwtServiceUseCaseImpl(
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServicesDescriptor)
                        ),
                        ExecutorImpl()
                )

        @Throws(VCLError::class)
        fun provideKeyServiceUseCase(
                context: Context,
                keyServicesDescriptor: VCLKeyServicesDescriptor
        ): KeyServiceUseCase =
                KeyServiceUseCaseImpl(
                        KeyServiceRepositoryImpl(
                                chooseKeyService(context, keyServicesDescriptor)
                        ),
                        ExecutorImpl()
                )
}