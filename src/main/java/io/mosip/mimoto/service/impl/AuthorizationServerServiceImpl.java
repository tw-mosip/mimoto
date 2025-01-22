package io.mosip.mimoto.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.dto.mimoto.AuthorizationServerWellKnownResponse;
import io.mosip.mimoto.exception.InvalidWellknownResponseException;
import io.mosip.mimoto.exception.PlatformErrorMessages;
import io.mosip.mimoto.service.AuthorizationServerService;
import io.mosip.mimoto.util.RestApiClient;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;

@Slf4j
@Service
public class AuthorizationServerServiceImpl implements AuthorizationServerService {

    @Autowired
    private Validator validator;

    @Autowired
    private RestApiClient restApiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "authServerWellknown", key = "{#authorizationServerHostUrl}")
    public AuthorizationServerWellKnownResponse getWellknown(String authorizationServerHostUrl) {
        try {
            String wellknownEndpoint = authorizationServerHostUrl + "/.well-known/oauth-authorization-server";
            log.info("fetching Authorization Server Wellknown by calling :: "+wellknownEndpoint);
            URI uri = URI.create(wellknownEndpoint);
            String wellknownResponse = restApiClient.getApi(uri, String.class);
            AuthorizationServerWellKnownResponse authorizationServerWellKnownResponse = objectMapper.readValue(wellknownResponse, AuthorizationServerWellKnownResponse.class);
            validate(authorizationServerWellKnownResponse);

            return authorizationServerWellKnownResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void validate(AuthorizationServerWellKnownResponse response) throws InvalidWellknownResponseException {
        Set<ConstraintViolation<AuthorizationServerWellKnownResponse>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation failed:");
            for (ConstraintViolation<AuthorizationServerWellKnownResponse> violation : violations) {
                sb.append("\n").append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
            }

            String errorCode = PlatformErrorMessages.INVALID_AUTHORIZATION_SERVER_WELLKNOWN_RESPONSE_EXCEPTION.getCode();
            String errorMsg = PlatformErrorMessages.INVALID_AUTHORIZATION_SERVER_WELLKNOWN_RESPONSE_EXCEPTION.getMessage() + "\n" + sb;
            throw new InvalidWellknownResponseException(errorCode, errorMsg);
        }
    }
}


