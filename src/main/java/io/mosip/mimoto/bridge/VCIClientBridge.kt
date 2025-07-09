package io.mosip.mimoto.bridge

import io.mosip.vciclient.VCIClient
import io.mosip.vciclient.clientMetadata.ClientMetadata
import io.mosip.vciclient.credentialResponse.CredentialResponse
import io.mosip.vciclient.issuerMetadata.IssuerMetadata
import kotlinx.coroutines.runBlocking

class VCIClientBridge {

    companion object {
        fun calculate2Sync(
            callback: (Int, Int) -> Int,
            callback2: (Int) -> Int
        ): Int = runBlocking {
            VCIClient("kjw").calculate2Fun(
                { x, y -> callback(x, y) },
                { z -> callback2(z) }
            )
        }

        fun calculateSync(callback: (Int) -> Int): Int {
            return runBlocking {
                VCIClient("kjw").calculate { callback(it) }
            }
        }

        fun requestCredentialFromTrustedIssuerBridgeCaller(
            traceabilityId: String,
            issuerMetadata: IssuerMetadata,
            clientMetadata: ClientMetadata,
            getProofJwt: (
                accessToken: String,
                cNonce: String?,
                issuerMetadata: Map<String, *>?,
                credentialConfigurationId: String?,
            ) -> String,
            getAuthCode: (String) -> String,
            downloadTimeoutInMillis: Long = 30000
        ): CredentialResponse? = runBlocking {
            VCIClient(traceabilityId).requestCredentialFromTrustedIssuer(
                issuerMetadata,
                clientMetadata,
                getProofJwt,
                getAuthCode,
                downloadTimeoutInMillis
            )
        }

        fun requestCredentialFromTrustedIssuerBridge(
            traceabilityId: String,
            issuerMetadata: IssuerMetadata,
            clientMetadata: ClientMetadata,
            getProofJwt: CredentialProofJwtFunction,
            getAuthCode: (String) -> String,
            downloadTimeoutInMillis: Long = 30000
        ): CredentialResponse? = runBlocking {
            val vciClient = VCIClient(traceabilityId)
            val credentialResponse: CredentialResponse? = vciClient.requestCredentialFromTrustedIssuer(
                issuerMetadata,
                clientMetadata,
                { accessToken, cNonce, issuerMeta, credentialConfigId ->
                    getProofJwt.apply(accessToken, cNonce, issuerMeta, credentialConfigId)
                },
                { authorizationEndpoint ->
                    getAuthCode(authorizationEndpoint)
                },
                downloadTimeoutInMillis
            )
            return@runBlocking credentialResponse
        }

        // Define a functional interface for the 4-argument getProofJwt
        fun interface CredentialProofJwtFunction {
            fun apply(
                accessToken: String,
                cNonce: String?,
                issuerMetadata: Map<String, *>?,
                credentialConfigurationId: String?
            ): String
        }
    }
}