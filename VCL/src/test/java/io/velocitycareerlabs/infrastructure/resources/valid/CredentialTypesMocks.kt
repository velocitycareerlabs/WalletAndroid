package io.velocitycareerlabs.infrastructure.resources.valid

/**
 * Created by Michael Avoyan on 4/30/21.
 */
class CredentialTypesMocks {
    companion object {
        const val CredentialType1 = "{\"credentialType\":\"EducationDegree\",\"schemaName\":\"education-degree\",\"recommended\":false,\"id\":\"5fe4a315d8b45dd2e80bd739\",\"createdAt\":\"2022-03-17T09:24:38.448Z\",\"updatedAt\":\"2022-03-17T09:24:38.448Z\"}"
        const val CredentialType2 = "{\"credentialType\":\"CurrentEmploymentPosition\",\"schemaName\":\"current-employment-position\",\"recommended\":true,\"id\":\"5fe4a315d8b45dd2e80bd73a\",\"createdAt\":\"2022-03-17T09:24:38.448Z\",\"updatedAt\":\"2022-03-17T09:24:38.448Z\"}"
        const val CredentialType3 = "{\"credentialType\":\"PastEmploymentPosition\",\"schemaName\":\"past-employment-position\",\"recommended\":false,\"id\":\"5fe4a315d8b45dd2e80bd73b\",\"createdAt\":\"2022-03-17T09:24:38.448Z\",\"updatedAt\":\"2022-03-17T09:24:38.448Z\"}"
        const val CredentialTypesJson = "[$CredentialType1, $CredentialType2, $CredentialType3]"
    }
}