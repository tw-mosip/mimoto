package io.mosip.mimoto.service;

import io.mosip.mimoto.dto.mimoto.CredentialIssuerWellKnownResponse;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.exception.AuthorizationServerWellknownResponseException;
import io.mosip.mimoto.exception.InvalidWellknownResponseException;

import java.io.IOException;

public interface IssuerWellknownService {
    CredentialIssuerWellKnownResponse getWellknown(String issuerId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException;
}
