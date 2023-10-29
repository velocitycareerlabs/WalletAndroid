/**
 * Created by Michael Avoyan on 25/10/2023.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.domain.models

import io.velocitycareerlabs.api.entities.VCLServiceTypesDynamic

internal interface ServiceTypesModel: Model<VCLServiceTypesDynamic>, Initializable<VCLServiceTypesDynamic>