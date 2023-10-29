/**
 * Created by Michael Avoyan on 29/10/2023.
 */

package io.velocitycareerlabs.infrastructure.resources.valid

class ServiceTypesMocks {
    companion object {
        const val ServiceTypesJsonStr =
            "{\"serviceTypes\":[{\"serviceType\":\"VlcInspector_v1\",\"serviceCategory\":\"Inspector\",\"notary\":false},{\"serviceType\":\"VlcNotaryIssuer_v1\",\"serviceCategory\":\"NotaryIssuer\",\"notary\":true,\"credentialGroup\":\"Contact\"},{\"serviceType\":\"VlcHolderAppProvider_v1\",\"serviceCategory\":\"HolderAppProvider\",\"notary\":false},{\"serviceType\":\"VlcNodeOperator_v1\",\"serviceCategory\":\"NodeOperator\",\"notary\":false},{\"serviceType\":\"VlcCredentialAgentOperator_v1\",\"serviceCategory\":\"CredentialAgentOperator\",\"notary\":false},{\"serviceType\":\"VlcCareerIssuer_v1\",\"serviceCategory\":\"Issuer\",\"notary\":false,\"credentialGroup\":\"Career\"},{\"serviceType\":\"VlcIdDocumentIssuer_v1\",\"serviceCategory\":\"IdDocumentIssuer\",\"notary\":false,\"credentialGroup\":\"IdDocument\"},{\"serviceType\":\"VlcNotaryIdDocumentIssuer_v1\",\"serviceCategory\":\"NotaryIdDocumentIssuer\",\"notary\":true,\"credentialGroup\":\"IdDocument\"},{\"serviceType\":\"VlcContactIssuer_v1\",\"serviceCategory\":\"ContactIssuer\",\"notary\":false,\"credentialGroup\":\"Contact\"},{\"serviceType\":\"VlcNotaryContactIssuer_v1\",\"serviceCategory\":\"NotaryContactIssuer\",\"notary\":true,\"credentialGroup\":\"Contact\"}]}"
    }
}