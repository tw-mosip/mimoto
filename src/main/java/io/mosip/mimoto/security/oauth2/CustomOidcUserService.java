package io.mosip.mimoto.security.oauth2;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.mosip.mimoto.util.JoseUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomOidcUserService extends OidcUserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOidcUserService.class);

    @Autowired
    @Qualifier("plainRestTemplate")
    private RestTemplate oauth2RestTemplate;


    @Autowired
    private JoseUtil joseUtil;

    // JWK set for signature verification
    private JWKSet jwkSet;

    @PostConstruct
    public void initialize() {
        try {
            this.jwkSet = JWKSet.load(new URL("https://esignet-mosipid.dev2.mosip.net/.well-known/jwks.json"));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA private key for JWE decryption from P12 file", e);
        }
    }

    // Implement this method to extract authorities from the JWT claims
    private List<GrantedAuthority> extractAuthorities(JWTClaimsSet claimsSet) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Example: If roles are in a claim called "roles" (adjust based on your JWT)
        if (claimsSet.getClaim("roles") instanceof List) {
            List<String> roles = (List<String>) claimsSet.getClaim("roles");
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
        } else if (claimsSet.getClaim("roles") instanceof String) {
            String role = (String) claimsSet.getClaim("roles");
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        }
        // Add other authority extraction logic if needed
        return authorities;
    }

    @Override
    public DefaultOidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String clientId = userRequest.getClientRegistration().getRegistrationId();
        String token = userRequest.getAccessToken().getTokenValue().substring(0, Math.min(20, userRequest.getAccessToken().getTokenValue().length())) + "...";
        log.info("Loading user for client: {}, token: {}", clientId, token);

        if ("esignet".equals(clientId)) {
            try {
                // Execute userinfo request
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
                headers.setAccept(List.of(MediaType.valueOf("application/jwt")));
                RequestEntity<?> request = RequestEntity
                        .get(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())
                        .headers(headers)
                        .build();
                String responseString = oauth2RestTemplate.exchange(request, String.class).getBody();
                log.debug("Raw Userinfo Response (JWS): {}", responseString);

                SignedJWT signedJWT = null;

                try {
                    signedJWT = SignedJWT.parse(responseString);
                    //verifySignature( signedJWT)
                    log.debug("Successfully parsed as JWS.");
                } catch (ParseException e) {
                    log.error("Failed to parse userinfo response as JWS: {}", e.getMessage(), e);
                    throw new OAuth2AuthenticationException(
                            new OAuth2Error("invalid_userinfo_response", "Failed to parse userinfo as JWS: " + e.getMessage(), null), e);
                }

                JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
                log.debug("Userinfo claims: {}", claimsSet.toJSONObject());

                // Map claims to attributes
                Map<String, Object> attributes = new HashMap<>(claimsSet.toJSONObject());
                attributes.put("sub", claimsSet.getSubject());
                List<GrantedAuthority> authorities = extractAuthorities(claimsSet);

                // Create OidcIdToken
                OidcIdToken idToken = new OidcIdToken(
                        userRequest.getIdToken().getTokenValue(),
                        userRequest.getIdToken().getIssuedAt(),
                        userRequest.getIdToken().getExpiresAt(),
                        claimsSet.getClaims()
                );

                return new DefaultOidcUser(
                        authorities,
                        idToken,
                        userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName() != null
                                ? userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
                                : "sub" // Default to "sub" if no name attribute is configured
                );

            } catch (OAuth2AuthenticationException e) {
                throw e; // Re-throw our specific OAuth2 exceptions
            } catch (Exception e) {
                log.error("An unexpected error occurred while loading userinfo: {}", e.getMessage(), e);
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("invalid_userinfo_response", "Failed to load userinfo: " + e.getMessage(), null), e);
            }
        }

        // For non-esignet (e.g., Google), use default parsing
        OidcUser oidcUser = super.loadUser(userRequest);
        return new DefaultOidcUser(
                oidcUser.getAuthorities(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(), // This might be null if not fetched
                oidcUser.getName()
        );
    }

    private boolean verifySignature(SignedJWT signedJWT) throws JOSEException {
        String kid = signedJWT.getHeader().getKeyID();
        RSAKey publicKey = (RSAKey) jwkSet.getKeyByKeyId(kid);
        if (publicKey == null) {
            log.error("No public key found for kid: {}", kid);
            return false;
        }
        return signedJWT.verify(new com.nimbusds.jose.crypto.RSASSAVerifier(publicKey));
    }


}