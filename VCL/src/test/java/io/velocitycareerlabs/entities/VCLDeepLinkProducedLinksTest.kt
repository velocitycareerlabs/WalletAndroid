/**
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLDeepLink
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URLDecoder

// Links as the Credentialing Hub, the credential agent and the holder app produce them, and what the SDK reads from
// each today. The request URI is compared as a server reads it: split at `&`, then at the first `=`, with each part
// form-decoded, in any order. Comments starting "Should be fixed" mark behaviour that is wrong today.
class VCLDeepLinkProducedLinksTest {

    private class Case(
        val name: String,
        val link: String,
        val did: String?,
        val vendorOriginContext: String?,
        val requestUri: String?,
        val query: List<Pair<String, String>>
    )

    private val cases = listOf(
        // Each credential_types value of the request URI is kept.
        Case(
            name = "Hub depot issuing link with four credential types",
            link = "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevcih.velocitycareerlabs.io%2Fvn-api%2Fr%2Fdid%3Aweb%3Adevregistrar.velocitynetwork.foundation%3Ad%3Aexample-issuer.com%2Fget-credential-manifest%3Fid%3D65f0c0ffee0000000000e1f2%26credential_types%3DPassportV1.0%26credential_types%3DNationalIdCardV1.0%26credential_types%3DDriversLicenseV1.0%26credential_types%3DIdDocumentV1.0&issuerDid=did%3Aweb%3Adevregistrar.velocitynetwork.foundation%3Ad%3Aexample-issuer.com&vendorOriginContext=depot%3A65f0c0ffee0000000000d3a1%3AaB3-x_Y9zQ0-k_L7",
            did = "did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com",
            vendorOriginContext = "depot:65f0c0ffee0000000000d3a1:aB3-x_Y9zQ0-k_L7",
            requestUri = "https://devcih.velocitycareerlabs.io/vn-api/r/did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com/get-credential-manifest",
            query = listOf(
                "credential_types" to "DriversLicenseV1.0",
                "credential_types" to "IdDocumentV1.0",
                "credential_types" to "NationalIdCardV1.0",
                "credential_types" to "PassportV1.0",
                "id" to "65f0c0ffee0000000000e1f2",
                "issuerDid" to "did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com",
                "vendorOriginContext" to "depot:65f0c0ffee0000000000d3a1:aB3-x_Y9zQ0-k_L7"
            )
        ),
        Case(
            name = "Hub depot issuing link with no credential types",
            link = "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevcih.velocitycareerlabs.io%2Fvn-api%2Fr%2Fdid%3Aweb%3Adevregistrar.velocitynetwork.foundation%3Ad%3Aexample-issuer.com%2Fget-credential-manifest%3Fid%3D65f0c0ffee0000000000e1f2&issuerDid=did%3Aweb%3Adevregistrar.velocitynetwork.foundation%3Ad%3Aexample-issuer.com&vendorOriginContext=depot%3A65f0c0ffee0000000000d3a1%3AaB3-x_Y9zQ0-k_L7",
            did = "did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com",
            vendorOriginContext = "depot:65f0c0ffee0000000000d3a1:aB3-x_Y9zQ0-k_L7",
            requestUri = "https://devcih.velocitycareerlabs.io/vn-api/r/did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com/get-credential-manifest",
            query = listOf(
                "id" to "65f0c0ffee0000000000e1f2",
                "issuerDid" to "did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com",
                "vendorOriginContext" to "depot:65f0c0ffee0000000000d3a1:aB3-x_Y9zQ0-k_L7"
            )
        ),
        Case(
            name = "Hub shared issuing link, without depot, credential types or vendorOriginContext",
            link = "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevcih.velocitycareerlabs.io%2Fvn-api%2Fr%2Fdid%3Aweb%3Adevregistrar.velocitynetwork.foundation%3Ad%3Aexample-issuer.com%2Fget-credential-manifest%3Fid%3D65f0c0ffee0000000000e1f2&issuerDid=did%3Aweb%3Adevregistrar.velocitynetwork.foundation%3Ad%3Aexample-issuer.com",
            did = "did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com",
            vendorOriginContext = null,
            requestUri = "https://devcih.velocitycareerlabs.io/vn-api/r/did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com/get-credential-manifest",
            query = listOf(
                "id" to "65f0c0ffee0000000000e1f2",
                "issuerDid" to "did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com"
            )
        ),
        Case(
            name = "Hub depot presentation link",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevcih.velocitycareerlabs.io%2Fvn-api%2Fr%2Fdid%3Aweb%3Adevregistrar.velocitynetwork.foundation%3Ad%3Aexample-issuer.com%2Fget-presentation-request%3Fid%3D65f0c0ffee0000000000e1f2&inspectorDid=did%3Aweb%3Adevregistrar.velocitynetwork.foundation%3Ad%3Aexample-issuer.com&vendorOriginContext=depot%3A65f0c0ffee0000000000d3a1",
            did = "did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com",
            vendorOriginContext = "depot:65f0c0ffee0000000000d3a1",
            requestUri = "https://devcih.velocitycareerlabs.io/vn-api/r/did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com/get-presentation-request",
            query = listOf(
                "id" to "65f0c0ffee0000000000e1f2",
                "inspectorDid" to "did:web:devregistrar.velocitynetwork.foundation:d:example-issuer.com",
                "vendorOriginContext" to "depot:65f0c0ffee0000000000d3a1"
            )
        ),
        Case(
            name = "Agent exchange issuing link",
            link = "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Fissue%2Fget-credential-manifest%3Fid%3D6630aa11bb22cc33dd44ee55%26exchange_id%3D6630ff00ee11dd22cc33bb44%26credential_types%3DPastEmploymentPosition&issuerDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=abc123",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "abc123",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/issue/get-credential-manifest",
            query = listOf(
                "credential_types" to "PastEmploymentPosition",
                "exchange_id" to "6630ff00ee11dd22cc33bb44",
                "id" to "6630aa11bb22cc33dd44ee55",
                "issuerDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "abc123"
            )
        ),
        // The page leaves out issuerDid, so the DID comes from the request URI's path.
        Case(
            name = "Agent issuing link rebuilt by the agent's /app-redirect page",
            link = "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Fissue%2Fget-credential-manifest%3Fid%3D6630aa11bb22cc33dd44ee55%26exchange_id%3D6630ff00ee11dd22cc33bb44%26credential_types%3DPastEmploymentPosition&vendorOriginContext=abc123",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "abc123",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/issue/get-credential-manifest",
            query = listOf(
                "credential_types" to "PastEmploymentPosition",
                "exchange_id" to "6630ff00ee11dd22cc33bb44",
                "id" to "6630aa11bb22cc33dd44ee55",
                "vendorOriginContext" to "abc123"
            )
        ),
        // The agent repeats EmailV1.0. The request URI carries it once.
        Case(
            name = "Agent exchange issuing link with two credential types",
            link = "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Fissue%2Fget-credential-manifest%3Fid%3D6630aa11bb22cc33dd44ee55%26exchange_id%3D6630ff00ee11dd22cc33bb44%26credential_types%3DEmailV1.0%26credential_types%3DPhoneV1.0%26credential_types%3DEmailV1.0&issuerDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=abc123",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "abc123",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/issue/get-credential-manifest",
            query = listOf(
                "credential_types" to "EmailV1.0",
                "credential_types" to "PhoneV1.0",
                "exchange_id" to "6630ff00ee11dd22cc33bb44",
                "id" to "6630aa11bb22cc33dd44ee55",
                "issuerDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "abc123"
            )
        ),
        Case(
            name = "Agent disclosure issuing link without vendorOriginContext",
            link = "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Fissue%2Fget-credential-manifest%3Fid%3D6630aa11bb22cc33dd44ee55&issuerDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = null,
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/issue/get-credential-manifest",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "issuerDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA"
            )
        ),
        Case(
            name = "Agent presentation link from a did:web inspector",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aweb%3Aexample.com%3Aissuers%3Aacme%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aweb%3Aexample.com%3Aissuers%3Aacme&vendorOriginContext=abc123",
            did = "did:web:example.com:issuers:acme",
            vendorOriginContext = "abc123",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:web:example.com:issuers:acme/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:web:example.com:issuers:acme",
                "vendorOriginContext" to "abc123"
            )
        ),
        Case(
            name = "vendorOriginContext with JSON",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=%7B%22SubjectKey%22%3A%7B%22BusinessUnit%22%3A%22ZC%22%2C%22KeyCode%22%3A%2254514480%22%7D%2C%22Token%22%3A%22832077a4%22%7D",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "{\"SubjectKey\":{\"BusinessUnit\":\"ZC\",\"KeyCode\":\"54514480\"},\"Token\":\"832077a4\"}",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "{\"SubjectKey\":{\"BusinessUnit\":\"ZC\",\"KeyCode\":\"54514480\"},\"Token\":\"832077a4\"}"
            )
        ),
        Case(
            name = "vendorOriginContext with commas",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=a%2Cb%2Cc",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "a,b,c",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "a,b,c"
            )
        ),
        Case(
            name = "vendorOriginContext with base64 = padding",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=dGVzdA%2B%2Fx9Q%3D%3D",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "dGVzdA+/x9Q==",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "dGVzdA+/x9Q=="
            )
        ),
        // Should be fixed: the value is cut at `?`, and `a=1` and `b=2` become parameters of the request URI. The
        // link is decoded before it's split at `?` and `&`.
        Case(
            name = "vendorOriginContext with ? and &",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=https%3A%2F%2Fexample.com%2Fcb%3Fa%3D1%26b%3D2",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "https://example.com/cb",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "a" to "1",
                "b" to "2",
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "https://example.com/cb"
            )
        ),
        Case(
            name = "vendorOriginContext with a space",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=John+Smith",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "John Smith",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "John Smith"
            )
        ),
        Case(
            name = "vendorOriginContext with % and a space",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=100%25+done",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "100% done",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "100% done"
            )
        ),
        Case(
            name = "vendorOriginContext with accented letters and a space",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=Jos%C3%A9+M%C3%BCller",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "José Müller",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "José Müller"
            )
        ),
        Case(
            name = "Empty vendorOriginContext",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA"
            )
        ),
        Case(
            name = "vendorOriginContext with #",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=order%23123",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "order#123",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "order#123"
            )
        ),
        Case(
            name = "vendorOriginContext with an email address with +",
            link = "velocity-network-devnet://inspect?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Finspect%2Fget-presentation-request%3Fid%3D6630aa11bb22cc33dd44ee55&inspectorDid=did%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA&vendorOriginContext=jane.doe%2Bwallet%40example.com",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "jane.doe+wallet@example.com",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/inspect/get-presentation-request",
            query = listOf(
                "id" to "6630aa11bb22cc33dd44ee55",
                "inspectorDid" to "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
                "vendorOriginContext" to "jane.doe+wallet@example.com"
            )
        ),
        Case(
            name = "Holder app issuing-sequence part, passed without the scheme",
            link = "issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Aion%3AEiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA%2Fissue%2Fget-credential-manifest%3Fid%3D6630aa11bb22cc33dd44ee55%26exchange_id%3D6630ff00ee11dd22cc33bb99%26credential_types%3DPhoneV1.0&vendorOriginContext=second-ctx",
            did = "did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA",
            vendorOriginContext = "second-ctx",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:ion:EiApMLdMb4NPb8sae9-hXGHP79W1gisApVSE80USPEbtJA/issue/get-credential-manifest",
            query = listOf(
                "credential_types" to "PhoneV1.0",
                "exchange_id" to "6630ff00ee11dd22cc33bb99",
                "id" to "6630aa11bb22cc33dd44ee55",
                "vendorOriginContext" to "second-ctx"
            )
        ),
        // The app relies on this. The SDK finds vendorOriginContext inside request_uri because it decodes the
        // whole link before splitting it.
        Case(
            name = "Holder app email/phone verification link, with vendorOriginContext inside request_uri",
            link = "velocity-network-devnet://issue?request_uri=https%3A%2F%2Fdevagent.velocitycareerlabs.io%2Fapi%2Fholder%2Fv0.6%2Forg%2Fdid%3Avelocity%3A0xd4df29726d500f9b85bc6c7f1b3c021f16305692%2Fissue%2Fget-credential-manifest%3FvendorOriginContext%3DeyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjMifQ.abc-_def",
            did = "did:velocity:0xd4df29726d500f9b85bc6c7f1b3c021f16305692",
            vendorOriginContext = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjMifQ.abc-_def",
            requestUri = "https://devagent.velocitycareerlabs.io/api/holder/v0.6/org/did:velocity:0xd4df29726d500f9b85bc6c7f1b3c021f16305692/issue/get-credential-manifest",
            query = listOf(
                "vendorOriginContext" to "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjMifQ.abc-_def"
            )
        )
    )

    @Test
    fun testProducedLinks() {
        val mismatches = cases.flatMap { case ->
            val deepLink = VCLDeepLink(case.link)
            listOfNotNull(
                mismatch(case.name, "did", case.did, deepLink.did),
                mismatch(case.name, "vendorOriginContext", case.vendorOriginContext, deepLink.vendorOriginContext),
                mismatch(case.name, "request URI", case.requestUri, deepLink.requestUri?.substringBefore('?')),
                mismatch(case.name, "request URI query", sorted(case.query), serverQuery(deepLink.requestUri))
            )
        }
        assertEquals("", mismatches.joinToString("\n"))
    }

    private fun mismatch(name: String, what: String, expected: Any?, actual: Any?) =
        if (expected == actual) null else "$name: $what is $actual, expected $expected"

    private fun sorted(pairs: List<Pair<String, String>>) =
        pairs.sortedWith(compareBy({ it.first }, { it.second }))

    private fun serverQuery(uri: String?): List<Pair<String, String>> =
        sorted(
            uri?.substringAfter('?', "").orEmpty().split('&').filter { it.isNotEmpty() }
                .map { formDecode(it.substringBefore('=')) to formDecode(it.substringAfter('=', "")) }
        )

    private fun formDecode(value: String) =
        try {
            URLDecoder.decode(value, "UTF-8")
        } catch (e: IllegalArgumentException) {
            value
        }
}
