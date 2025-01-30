package io.mosip.mimoto.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
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
import io.mosip.mimoto.model.QRCodeType;
import io.mosip.mimoto.service.AuthorizationServerService;
import io.mosip.mimoto.service.IssuerWellknownService;
import io.mosip.mimoto.service.IssuersService;
import io.mosip.mimoto.util.QRCodeTypeDeserializer;
import io.mosip.mimoto.util.Utilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class IssuersServiceImpl implements IssuersService {

    @Autowired
    private Utilities utilities;

    @Autowired
    AuthorizationServerService authorizationServerService;

     @Autowired
     private IssuerWellknownService issuerWellknownService;

    @Override
    public IssuersDTO getAllIssuers(String search) throws ApiNotAccessibleException, AuthorizationServerWellknownResponseException, IOException, InvalidWellknownResponseException {
        IssuersDTO issuers;
        String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
        if (issuersConfigJsonValue == null) {
            throw new ApiNotAccessibleException();
        }

        Gson gsonWithIssuerDataOnlyFilter = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(QRCodeType.class, new QRCodeTypeDeserializer()).create();
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
        Gson gson = new Gson();
        return gsonWithIssuerDataOnlyFilter.fromJson(gson.toJson(updateAndValidateIssuersConfig(issuers)), IssuersDTO.class);
    }

    @Override
    public IssuersDTO getAllIssuersWithAllFields() throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        IssuersDTO issuers;
        String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
        if (issuersConfigJsonValue == null) {
            throw new ApiNotAccessibleException();
        }
        Gson gsonWithIssuerDataOnlyFilter = new GsonBuilder().registerTypeAdapter(QRCodeType.class, new QRCodeTypeDeserializer()).create();
        issuers = gsonWithIssuerDataOnlyFilter.fromJson(issuersConfigJsonValue, IssuersDTO.class);
        return updateAndValidateIssuersConfig(issuers);
    }


    @Override
    public IssuerDTO getIssuerConfig(String issuerId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        IssuerDTO issuerDTO = null;
        String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
        if (issuersConfigJsonValue == null) {
            throw new ApiNotAccessibleException();
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(QRCodeType.class, new QRCodeTypeDeserializer()).create();
        IssuersDTO issuers = gson.fromJson(issuersConfigJsonValue, IssuersDTO.class);
        Optional<IssuerDTO> issuerConfigResp = issuers.getIssuers().stream()
                .filter(issuer -> issuer.getIssuer_id().equals(issuerId))
                .findFirst();
        if (issuerConfigResp.isPresent())
            issuerDTO = issuerConfigResp.get();
        else
            throw new InvalidIssuerIdException();
        IssuersDTO issuersDTO = new IssuersDTO();
        issuersDTO.setIssuers(Arrays.asList(issuerDTO));
        return updateAndValidateIssuersConfig(issuersDTO).getIssuers().getFirst();
    }

    @Override
    @Cacheable(value = "issuerWellknown", key = "{#credentialIssuerHost}")
    public CredentialIssuerWellKnownResponse getIssuerWellknown(String credentialIssuerHost) throws ApiNotAccessibleException, IOException, InvalidWellknownResponseException, AuthorizationServerWellknownResponseException {
        try {
            return issuerWellknownService.getWellknown(credentialIssuerHost);
        } catch (JsonProcessingException | ApiNotAccessibleException |
                 InvalidWellknownResponseException e) {
            throw e;
        }
    }

    @Override
    public CredentialsSupportedResponse getIssuerWellknownForCredentialType(String issuerId, String credentialId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        String credentialIssuerHost = getCredentialIssuerHost(issuerId);
        CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = issuerWellknownService.getWellknown(credentialIssuerHost);
        CredentialsSupportedResponse credentialsSupportedResponse = credentialIssuerWellKnownResponse.getCredentialConfigurationsSupported().get(credentialId);
        if (credentialsSupportedResponse == null) {
            throw new ApiNotAccessibleException();
        }
        return credentialsSupportedResponse;
    }

    @Override
    public CredentialIssuerConfigurationResponse getIssuerConfiguration(String issuerId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        String credentialIssuerHost = getCredentialIssuerHost(issuerId);
        CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = issuerWellknownService.getWellknown(credentialIssuerHost);
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

    public String getCredentialIssuerHost(String issuerId) throws AuthorizationServerWellknownResponseException, ApiNotAccessibleException, IOException, InvalidWellknownResponseException {
        IssuerDTO issuerDTO = getIssuerConfig(issuerId);
        return issuerDTO.getCredential_issuer_host();
    }

    public IssuersDTO updateAndValidateIssuersConfig(IssuersDTO issuersDTO) throws AuthorizationServerWellknownResponseException, ApiNotAccessibleException, IOException, InvalidWellknownResponseException {
        List<String> errors = new ArrayList<>();

        for (IssuerDTO issuerDTO : issuersDTO.getIssuers()) {
            String issuerId = issuerDTO.getIssuer_id();
            String credentialIssuerHost = issuerDTO.getCredential_issuer_host();
            issuerDTO.setWellknown_endpoint(credentialIssuerHost + "/.well-known/openid-credential-issuer");
            CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = issuerWellknownService.getWellknown(credentialIssuerHost);
            updateIssuerWithAuthServerConfig(issuerDTO, credentialIssuerWellKnownResponse);

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

    public IssuerDTO updateIssuerWithAuthServerConfig(IssuerDTO issuerDTO, CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponses) throws AuthorizationServerWellknownResponseException, ApiNotAccessibleException, IOException {
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