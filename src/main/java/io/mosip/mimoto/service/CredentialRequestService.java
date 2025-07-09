package io.mosip.mimoto.service;

import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.mimoto.CredentialIssuerWellKnownResponse;
import io.mosip.mimoto.dto.mimoto.CredentialsSupportedResponse;
import io.mosip.mimoto.dto.mimoto.VCCredentialRequest;

public interface CredentialRequestService {
    VCCredentialRequest buildRequest(
            IssuerDTO issuerDTO,
            CredentialIssuerWellKnownResponse wellKnownResponse,
            CredentialsSupportedResponse credentialsSupportedResponse,
            String accessToken,
            String walletId,
            String base64EncodedWalletKey,
            Boolean isLoginFlow
    ) throws Exception;

    String generateProofJWT(IssuerDTO issuerDTO, CredentialIssuerWellKnownResponse wellKnownResponse, CredentialsSupportedResponse credentialsSupportedResponse, String accessToken, String walletId, String base64EncodedWalletKey, boolean isLoginFlow) throws Exception;
}