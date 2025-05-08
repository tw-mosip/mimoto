package io.mosip.mimoto.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver delegate;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorize");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request);
        return customizeAuthorizationRequest(authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(authorizationRequest);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }

        log.info("Original authorization request: clientId={}, additionalParameters={}",
                authorizationRequest.getClientId(),
                authorizationRequest.getAdditionalParameters());

        if ("mpartner-mosipid-mimoto-dev2".equals(authorizationRequest.getClientId())) {
            Map<String, Object> additionalParameters = new HashMap<>(authorizationRequest.getAdditionalParameters());
            additionalParameters.put("acr_values", "mosip:idp:acr:generated-code");
            additionalParameters.put("claims", "{\"userinfo\":{\"name\":{\"essential\":true},\"email\":{\"essential\":true},\"phone_number\":{\"essential\":false},\"picture\":{\"essential\":false}},\"id_token\":{}}");
            additionalParameters.put("claims_locales", "en");
            additionalParameters.put("display", "page");
            additionalParameters.put("ui_locales", "en-US");

            OAuth2AuthorizationRequest modifiedRequest = OAuth2AuthorizationRequest.from(authorizationRequest)
                    .additionalParameters(additionalParameters)
                    .build();

            log.info("Modified authorization request: clientId={}, additionalParameters={}",
                    modifiedRequest.getClientId(),
                    modifiedRequest.getAdditionalParameters());
            return modifiedRequest;
        }

        return authorizationRequest;
    }
}
