/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.impl.data.usecases

import io.velocitycareerlabs.api.entities.VCLResult

internal typealias VCLAsyncResult<T> = ((VCLResult<T>) -> Unit) -> Unit

internal fun <T> vclAsyncResult(block: ((VCLResult<T>) -> Unit) -> Unit): VCLAsyncResult<T> = block

internal fun <T> vclAsyncSuccess(value: T): VCLAsyncResult<T> =
    { completion -> completion(VCLResult.Success(value)) }

internal fun <T, U> VCLAsyncResult<T>.then(next: (T) -> VCLAsyncResult<U>): VCLAsyncResult<U> =
    { completion ->
        this { result ->
            when (result) {
                is VCLResult.Failure -> completion(VCLResult.Failure(result.error))
                is VCLResult.Success -> next(result.data)(completion)
            }
        }
    }

internal fun <T, U> VCLAsyncResult<T>.map(transform: (T) -> U): VCLAsyncResult<U> =
    then { value -> vclAsyncSuccess(transform(value)) }
