package io.mosip.mimoto.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.IssuersDTO;
import io.mosip.mimoto.dto.mimoto.AuthorizationServerWellKnownResponse;
import io.mosip.mimoto.dto.mimoto.CredentialIssuerConfigurationResponse;
import io.mosip.mimoto.dto.mimoto.CredentialIssuerWellKnownResponse;
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
    private AuthorizationServerService authorizationServerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IssuerWellknownService issuerWellknownService;

    @Override
    public IssuersDTO getIssuers(String search) throws ApiNotAccessibleException, AuthorizationServerWellknownResponseException, IOException, InvalidWellknownResponseException {
        IssuersDTO issuersDTO = getAllIssuers();
        getAllEnabledIssuers(issuersDTO);
        getFilteredIssuers(issuersDTO, search);

        //validate Issuers Auth server config
        List<String> errors = new ArrayList<>();
        for (IssuerDTO issuerDTO : issuersDTO.getIssuers()) {
            String validationErrorMsg = validateField(issuerDTO);
            if (!validationErrorMsg.isEmpty()) errors.add(validationErrorMsg);
        }

        if (!errors.isEmpty()) {
            throw new AuthorizationServerWellknownResponseException("Validations Failed : " + String.join(", ", errors));
        }

        for (int i = 0; i < issuersDTO.getIssuers().size(); i++) {
            IssuerDTO issuerDTO = issuersDTO.getIssuers().get(i);
            Map<String, Object> map = objectMapper.convertValue(issuerDTO, Map.class);
            map.remove("authorization_audience");
            IssuerDTO updatedIssuer = objectMapper.convertValue(map, IssuerDTO.class);
            issuersDTO.getIssuers().set(i, updatedIssuer);
        }

        return issuersDTO;
    }

    @Override
    public IssuerDTO getIssuerDetails(String issuerId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        IssuersDTO issuersDTO = getAllIssuers();
        getAllEnabledIssuers(issuersDTO);

        Optional<IssuerDTO> issuerConfigResp = issuersDTO.getIssuers().stream()
                .filter(issuer -> issuer.getIssuer_id().equals(issuerId))
                .findFirst();

        if (issuerConfigResp.isPresent()) {
            IssuerDTO issuerDTO = issuerConfigResp.get();
            String validationErrorMsg = validateField(issuerDTO);
            if (!validationErrorMsg.isEmpty()) {
                throw new AuthorizationServerWellknownResponseException("Validations Failed : " + String.join(", ", validationErrorMsg));
            }
            return issuerDTO;
        } else {
            throw new InvalidIssuerIdException();
        }
    }

    public void getAllEnabledIssuers(IssuersDTO issuersDTO) {
        List<IssuerDTO> enabledIssuers = issuersDTO.getIssuers().stream()
                .filter(issuer -> "true".equals(issuer.getEnabled()))
                .collect(Collectors.toList());
        issuersDTO.setIssuers(enabledIssuers);
    }

    public void getFilteredIssuers(IssuersDTO issuersDTO, String search) {
        if (!StringUtils.isEmpty(search)) {
            List<IssuerDTO> filteredIssuers = issuersDTO.getIssuers().stream()
                    .filter(issuer -> issuer.getDisplay().stream()
                            .anyMatch(displayDTO -> displayDTO.getTitle().toLowerCase().contains(search.toLowerCase())))
                    .collect(Collectors.toList());
            issuersDTO.setIssuers(filteredIssuers);
        }
    }

    @Override
    public IssuersDTO getAllIssuers() throws ApiNotAccessibleException, AuthorizationServerWellknownResponseException, IOException, InvalidWellknownResponseException {
        IssuersDTO issuersDTO;
        String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
        if (issuersConfigJsonValue == null) {
            throw new ApiNotAccessibleException();
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(QRCodeType.class, new QRCodeTypeDeserializer()).create();
        issuersDTO = gson.fromJson(issuersConfigJsonValue, IssuersDTO.class);
        for (IssuerDTO issuerDTO : issuersDTO.getIssuers()) {
            updateIssuerWithAuthServerConfig(issuerDTO);
        }

        return issuersDTO;
    }

    @Override
    @Cacheable(value = "credentialIssuerConfig", key = "{#issuerId}")
    public CredentialIssuerConfigurationResponse getIssuerConfiguration(String issuerId) throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        String credentialIssuerHost = getIssuerDetails(issuerId).getCredential_issuer_host();
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

    public void updateIssuerWithAuthServerConfig(IssuerDTO issuerDTO) throws AuthorizationServerWellknownResponseException, ApiNotAccessibleException, IOException, InvalidWellknownResponseException {
        CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = issuerWellknownService.getWellknown(issuerDTO.getCredential_issuer_host());
        AuthorizationServerWellKnownResponse authorizationServerWellKnownResponse = getAuthorizationServerWellknown(credentialIssuerWellKnownResponse.getAuthorizationServers().get(0));
        String tokenEndpoint = authorizationServerWellKnownResponse.getTokenEndpoint();
        issuerDTO.setAuthorization_audience(tokenEndpoint);
        issuerDTO.setProxy_token_endpoint(tokenEndpoint);
    }

    private String validateField(IssuerDTO issuerDTO) {
        String issuerId = issuerDTO.getIssuer_id();
        Map<String, String> validationFieldsMap = Map.of(
                "authorization_audience", issuerDTO.getAuthorization_audience(),
                "proxy_token_endpoint", issuerDTO.getProxy_token_endpoint()
        );

        StringBuilder issuerErrors = new StringBuilder();

        validationFieldsMap.forEach((key, value) -> {
            if (value == null || value.trim().isEmpty()) {
                if (issuerErrors.length() == 0) {
                    issuerErrors.append("Issuer ID : ").append(issuerId).append(" -> ");
                }
                issuerErrors.append(key).append(" cannot be empty or contain only whitespace\n");
            }
        });

        return issuerErrors.toString();
    }
}