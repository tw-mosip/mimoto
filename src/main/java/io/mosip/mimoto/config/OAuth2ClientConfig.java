package io.mosip.mimoto.config;

import io.mosip.mimoto.util.JoseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

@Configuration
@Slf4j
public class OAuth2ClientConfig {

    @Autowired
    JoseUtil joseUtil;

    @Bean
    public DefaultAuthorizationCodeTokenResponseClient tokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();

        OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter =
                new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        tokenResponseClient.setRequestEntityConverter(authorizationGrantRequest -> {
            ClientRegistration clientRegistration = authorizationGrantRequest.getClientRegistration();
            if ("private_key_jwt".equals(clientRegistration.getClientAuthenticationMethod().getValue())) {
                log.debug("Converting token request for private_key_jwt");
                String clientAssertion = null;
                try {
                    clientAssertion = joseUtil.getJWT("mpartner-mosipid-mimoto-dev2", "/Users/gurpreet.kaur/MOSIP/develop/mimoto/certs/", "dev2oidckeystore.p12", "mpartner-mosipid-mimoto-dev2", "mosip123", "https://esignet-mosipid.dev2.mosip.net/v1/esignet/oauth/v2/token");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
                formParameters.add("grant_type", authorizationGrantRequest.getGrantType().getValue());
                formParameters.add("code", authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
                formParameters.add("redirect_uri", "http://localhost:8099/v1/mimoto/oauth2/callback/esignet");
                formParameters.add("client_id", clientRegistration.getClientId());
                formParameters.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
                formParameters.add("client_assertion", clientAssertion.replace("[", "").replace("]", ""));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                return RequestEntity.post(clientRegistration.getProviderDetails().getTokenUri())
                        .headers(headers)
                        .body(formParameters);
            }
            // Fallback to default converter for other methods (e.g., Google)

            return defaultConverter.convert(authorizationGrantRequest);
        });
        return tokenResponseClient;
    }
}