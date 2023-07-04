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
import io.velocitycareerlabs.impl.data.infrastructure.db.CacheServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.db.SecretStoreServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.network.NetworkServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.executors.ExecutorImpl
import io.velocitycareerlabs.impl.data.infrastructure.jwt.JwtServiceRemoteImpl
import io.velocitycareerlabs.impl.data.infrastructure.keys.KeyServiceImpl
import io.velocitycareerlabs.impl.data.infrastructure.keys.KeyServiceRemoteImpl
import io.velocitycareerlabs.impl.data.models.*
import io.velocitycareerlabs.impl.data.repositories.*
import io.velocitycareerlabs.impl.data.usecases.*
import io.velocitycareerlabs.impl.domain.infrastructure.jwt.JwtService
import io.velocitycareerlabs.impl.domain.infrastructure.keys.KeyService
import io.velocitycareerlabs.impl.domain.models.*
import io.velocitycareerlabs.impl.domain.usecases.*

internal object VclBlocksProvider {
        private fun chooseJwtService(
                context: Context,
                keyServiceType: VCLKeyServiceType
        ): JwtService {
                if (keyServiceType == VCLKeyServiceType.REMOTE) {
                        return JwtServiceRemoteImpl(NetworkServiceImpl())
                }
                return JwtServiceImpl(KeyServiceImpl(SecretStoreServiceImpl(context.applicationContext)))
        }

        private fun chooseKeyService(
                context: Context,
                keyServiceType: VCLKeyServiceType
        ): KeyService {
                if (keyServiceType == VCLKeyServiceType.REMOTE) {
                        return KeyServiceRemoteImpl(NetworkServiceImpl())
                }
                return KeyServiceImpl(SecretStoreServiceImpl(context.applicationContext))
        }

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

        fun provideCountryCodesModel(context: Context): CountriesModel =
                CountriesModelImpl(
                        CountriesUseCaseImpl(
                                CountriesRepositoryImpl(
                                        NetworkServiceImpl(),
                                        CacheServiceImpl(context)
                                ),
                                ExecutorImpl()
                        )
                )

        fun providePresentationRequestUseCase(
                context: Context,
                keyServiceType: VCLKeyServiceType
        ): PresentationRequestUseCase =
                PresentationRequestUseCaseImpl(
                        PresentationRequestRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ResolveKidRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServiceType)
                        ),
                        ExecutorImpl()
                )

        fun providePresentationSubmissionUseCase(
                context: Context,
                keyServiceType: VCLKeyServiceType
        ): PresentationSubmissionUseCase =
                PresentationSubmissionUseCaseImpl(
                        PresentationSubmissionRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServiceType)
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

        fun provideCredentialManifestUseCase(
                context: Context,
                keyServiceType: VCLKeyServiceType
        ): CredentialManifestUseCase =
                CredentialManifestUseCaseImpl(
                        CredentialManifestRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        ResolveKidRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServiceType)
                        ),
                        ExecutorImpl()
                )

        fun provideIdentificationSubmissionUseCase(
                context: Context,
                keyServiceType: VCLKeyServiceType
        ): IdentificationSubmissionUseCase =
                IdentificationSubmissionUseCaseImpl(
                        IdentificationSubmissionRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServiceType)
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

        fun provideFinalizeOffersUseCase(
                context: Context,
                keyServiceType: VCLKeyServiceType
        ): FinalizeOffersUseCase =
                FinalizeOffersUseCaseImpl(
                        FinalizeOffersRepositoryImpl(
                                NetworkServiceImpl()
                        ),
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServiceType)
                        ),
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

        fun provideJwtServiceUseCase(
                context: Context,
                keyServiceType: VCLKeyServiceType
        ): JwtServiceUseCase =
                JwtServiceUseCaseImpl(
                        JwtServiceRepositoryImpl(
                                chooseJwtService(context, keyServiceType)
                        ),
                        ExecutorImpl()
                )

        fun provideKeyServiceUseCase(
                context: Context,
                keyServiceType: VCLKeyServiceType
        ): KeyServiceUseCase =
                KeyServiceUseCaseImpl(
                        KeyServiceRepositoryImpl(
                                chooseKeyService(context, keyServiceType)
                        ),
                        ExecutorImpl()
                )
}