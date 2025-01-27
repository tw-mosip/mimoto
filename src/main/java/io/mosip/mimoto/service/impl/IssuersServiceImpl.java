package io.mosip.mimoto.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.IssuersDTO;
import io.mosip.mimoto.dto.mimoto.AuthorizationServerWellKnownResponse;
import io.mosip.mimoto.dto.mimoto.CredentialIssuerConfigurationResponse;
import io.mosip.mimoto.dto.mimoto.CredentialIssuerWellKnownResponse;
import io.mosip.mimoto.dto.mimoto.CredentialsSupportedResponse;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.exception.AuthorizationServerWellknownResponseException;
import io.mosip.mimoto.exception.InvalidIssuerIdException;
import io.mosip.mimoto.exception.InvalidWellknownResponseException;
import io.mosip.mimoto.service.AuthorizationServerService;
import io.mosip.mimoto.service.IssuersService;
import io.mosip.mimoto.util.CredentialIssuerWellknownResponseValidator;
import io.mosip.mimoto.util.RestApiClient;
import io.mosip.mimoto.util.Utilities;
import jakarta.validation.Validator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class IssuersServiceImpl implements IssuersService {

    @Autowired
    private Utilities utilities;

    @Autowired
    private Validator validator;

    @Autowired
    private RestApiClient restApiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CredentialIssuerWellknownResponseValidator credentialIssuerWellknownResponseValidator;

    @Autowired
    AuthorizationServerService authorizationServerService;


    @Override
    public IssuersDTO getAllIssuers(String search) throws ApiNotAccessibleException, AuthorizationServerWellknownResponseException, IOException, InvalidWellknownResponseException {
        IssuersDTO issuers;
        String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
        if (issuersConfigJsonValue == null) {
            throw new ApiNotAccessibleException();
        }
        Gson gsonWithIssuerDataOnlyFilter = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        issuers = gsonWithIssuerDataOnlyFilter.fromJson(issuersConfigJsonValue, IssuersDTO.class);
        List<IssuerDTO> enabledIssuers = issuers.getIssuers().stream()
                .filter(issuer -> "true".equals(issuer.getEnabled()))
                .collect(Collectors.toList());
        issuers.setIssuers(enabledIssuers);

        // Filter issuers list with search string
        if (!StringUtils.isEmpty(search)) {
            List<IssuerDTO> filteredIssuers = issuers.getIssuers().stream()
                    .filter(issuer -> issuer.getDisplay().stream()
                            .anyMatch(displayDTO -> displayDTO.getTitle().toLowerCase().contains(search.toLowerCase())))
                    .collect(Collectors.toList());
            issuers.setIssuers(filteredIssuers);
        }
        IssuersDTO issuersWithMissingFields = updateAndValidateIssuersConfig(issuers);
        return issuersWithMissingFields;
    }

    @Override
    public IssuersDTO getAllIssuersWithAllFields() throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        IssuersDTO issuers;
        String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
        if (issuersConfigJsonValue == null) {
            throw new ApiNotAccessibleException();
        }
        Gson gsonWithIssuerDataOnlyFilter = new GsonBuilder().create();
        issuers = gsonWithIssuerDataOnlyFilter.fromJson(issuersConfigJsonValue, IssuersDTO.class);
        IssuersDTO issuersWithMissingFields = updateAndValidateIssuersConfig(issuers);
        return issuersWithMissingFields;
    }


    @Override
    public IssuerDTO getIssuerConfig(String issuerId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        IssuerDTO issuerDTO = null;
        String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
        if (issuersConfigJsonValue == null) {
            throw new ApiNotAccessibleException();
        }
        IssuersDTO issuers = new Gson().fromJson(issuersConfigJsonValue, IssuersDTO.class);
        Optional<IssuerDTO> issuerConfigResp = issuers.getIssuers().stream()
                .filter(issuer -> issuer.getIssuer_id().equals(issuerId))
                .findFirst();
        if (issuerConfigResp.isPresent())
            issuerDTO = issuerConfigResp.get();
        else
            throw new InvalidIssuerIdException();
        IssuersDTO issuersDTO = new IssuersDTO();
        issuersDTO.setIssuers(Arrays.asList(issuerDTO));
        IssuersDTO issuersWithMissingFields = updateAndValidateIssuersConfig(issuersDTO);
        return issuersWithMissingFields.getIssuers().getFirst();
    }

    @Override
    public CredentialIssuerWellKnownResponse getIssuerWellknown(String issuerId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        try {
            IssuerDTO issuerDTO = getIssuerConfig(issuerId);
            String credentialIssuerHost = issuerDTO.getCredential_issuer_host();
            if (credentialIssuerHost == null) {
                throw new InvalidWellknownResponseException("credential_issuer_host cannot be null for issuer " + issuerId);
            }
            String wellknownEndpoint = credentialIssuerHost + "/issuance/.well-known/openid-credential-issuer";
            String wellknownResponse = restApiClient.getApi(wellknownEndpoint, String.class);
            CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = objectMapper.readValue(wellknownResponse, CredentialIssuerWellKnownResponse.class);
            credentialIssuerWellknownResponseValidator.validate(credentialIssuerWellKnownResponse, validator);
            return credentialIssuerWellKnownResponse;
        } catch (JsonProcessingException | ApiNotAccessibleException |
                 InvalidWellknownResponseException e) {
            throw e;
        }
    }

    public CredentialIssuerWellKnownResponse getIssuerWellknownWithUrl(String credentialIssuerHost) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        try {
            String issuerWellknownEndpoint = credentialIssuerHost + "/issuance/.well-known/openid-credential-issuer";
            String wellknownResponse = restApiClient.getApi(issuerWellknownEndpoint, String.class);
            CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = objectMapper.readValue(wellknownResponse, CredentialIssuerWellKnownResponse.class);
            credentialIssuerWellknownResponseValidator.validate(credentialIssuerWellKnownResponse, validator);
            return credentialIssuerWellKnownResponse;
        } catch (JsonProcessingException | ApiNotAccessibleException |
                 InvalidWellknownResponseException e) {
            throw e;
        }
    }

    @Override
    public CredentialsSupportedResponse getIssuerWellknownForCredentialType(String issuerId, String credentialId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = getIssuerWellknown(issuerId);
        CredentialsSupportedResponse credentialsSupportedResponse = credentialIssuerWellKnownResponse.getCredentialConfigurationsSupported().get(credentialId);
        if (credentialsSupportedResponse == null) {
            throw new ApiNotAccessibleException();
        }
        return credentialsSupportedResponse;
    }

    @Override
    public CredentialIssuerConfigurationResponse getIssuerConfiguration(String issuerId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = getIssuerWellknown(issuerId);
        String oauthServerUrl = credentialIssuerWellKnownResponse.getAuthorizationServers().get(0);
        AuthorizationServerWellKnownResponse authorizationServerWellKnownResponse = getAuthorizationServerWellknown(oauthServerUrl);

        return new CredentialIssuerConfigurationResponse(
                credentialIssuerWellKnownResponse.getCredentialIssuer(),
                credentialIssuerWellKnownResponse.getAuthorizationServers(),
                credentialIssuerWellKnownResponse.getCredentialEndPoint(),
                credentialIssuerWellKnownResponse.getCredentialConfigurationsSupported(),
                authorizationServerWellKnownResponse
        );
    }

    public AuthorizationServerWellKnownResponse getAuthorizationServerWellknown(String oauthServerUrl) throws AuthorizationServerWellknownResponseException {
        return authorizationServerService.getWellknown(oauthServerUrl);
    }

    public IssuersDTO updateAndValidateIssuersConfig(IssuersDTO issuersDTO) throws AuthorizationServerWellknownResponseException, ApiNotAccessibleException, IOException, InvalidWellknownResponseException {
        List<String> errors = new ArrayList<>();

        for (IssuerDTO issuerDTO : issuersDTO.getIssuers()) {
            String issuerId = issuerDTO.getIssuer_id();
            String credentialIssuerHost = issuerDTO.getCredential_issuer_host();

            if (credentialIssuerHost == null) {
                throw new InvalidWellknownResponseException("credential_issuer_host cannot be null for issuer " + issuerId);
            }

            CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = getIssuerWellknownWithUrl(credentialIssuerHost);
            addIssuersMissingConfig(issuerDTO, credentialIssuerWellKnownResponse);

            Map<String, String> fieldsMap = Map.of(
                    "authorization_audience", issuerDTO.getAuthorization_audience(),
                    "proxy_token_endpoint", issuerDTO.getProxy_token_endpoint()
            );

            validateField(fieldsMap, issuerId, errors);
        }

        if (!errors.isEmpty()) {
            throw new AuthorizationServerWellknownResponseException("Validations Failed : " + String.join(", ", errors));
        }

        return issuersDTO;
    }


    public IssuerDTO addIssuersMissingConfig(IssuerDTO issuerDTO, CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponses) throws AuthorizationServerWellknownResponseException, ApiNotAccessibleException, IOException {
        AuthorizationServerWellKnownResponse authorizationServerWellKnownResponse = getAuthorizationServerWellknown(credentialIssuerWellKnownResponses.getAuthorizationServers().get(0));
        String tokenEndpoint = authorizationServerWellKnownResponse.getTokenEndpoint();
        issuerDTO.setAuthorization_audience(tokenEndpoint);
        issuerDTO.setProxy_token_endpoint(tokenEndpoint);
        return issuerDTO;
    }

    private void validateField(Map<String, String> fieldsMap, String issuerId, List<String> errors) {
        StringBuilder issuerErrors = null;
        for (String key : fieldsMap.keySet()) {
            String fieldName = key;
            String fieldValue = fieldsMap.get(key);
            if (!(fieldValue != null && !fieldValue.trim().isEmpty())) {
                if (issuerErrors == null) {
                    issuerErrors = new StringBuilder("Issuer ID : " + issuerId + " -> ");
                }
                issuerErrors.append(fieldName + " cannot be empty or contain only whitespace" + "\n");
            }
        }
        if (issuerErrors != null) {
            errors.add(issuerErrors.toString());
        }
    }
}