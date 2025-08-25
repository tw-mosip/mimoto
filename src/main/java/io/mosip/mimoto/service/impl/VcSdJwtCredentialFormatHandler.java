package io.mosip.mimoto.service.impl;


import com.authlete.sd.Disclosure;
import com.authlete.sd.SDJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.constant.CredentialFormat;
import io.mosip.mimoto.dto.mimoto.*;
import io.mosip.mimoto.service.CredentialFormatHandler;
import io.mosip.mimoto.util.LocaleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.mosip.mimoto.util.IssuerConfigUtil.camelToTitleCase;
import static io.mosip.mimoto.util.JwtUtils.parseJwtPayload;

@Slf4j
@Component("vc+sd-jwt")
public class VcSdJwtCredentialFormatHandler implements CredentialFormatHandler {

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SD_DIGEST_KEY = "_sd";
    private static final String SD_ALG_KEY = "_sd_alg";
    private static final Pattern DIGEST_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{43}$"); // Base64url without padding

    @Override
    public String getSupportedFormat() {
        return CredentialFormat.VC_SD_JWT.getFormat();
    }


    @Override
    public VCCredentialRequest buildCredentialRequest(VCCredentialRequestProof proof, CredentialsSupportedResponse credentialsSupportedResponse) {
        return VCCredentialRequest.builder().format(getSupportedFormat()).proof(proof).vct(credentialsSupportedResponse.getVct()).build();
    }

    @Override
    public Map<String, Object> extractCredentialClaims(VCCredentialResponse vcCredentialResponse) {
        Object credential = vcCredentialResponse.getCredential();
        if (credential instanceof String) {
            return extractClaimsFromSdJwt((String) credential);
        }
        log.warn("Unexpected credential format in response for SD-JWT VC: {}", credential);
        return Collections.emptyMap();
    }

    @Override
    public LinkedHashMap<String, Map<CredentialIssuerDisplayResponse, Object>> loadDisplayPropertiesFromWellknown(
            Map<String, Object> credentialProperties,
            CredentialsSupportedResponse credentialsSupportedResponse,
            String userLocale) {

        LinkedHashMap<String, Map<CredentialIssuerDisplayResponse, Object>> displayProperties = new LinkedHashMap<>();

        // Extract raw claims and convert to DTOs
        Map<String, Object> rawClaims = Optional.ofNullable(credentialsSupportedResponse.getClaims())
                .map(map -> (map.size() == 1 && map.values().iterator().next() instanceof Map)
                        ? (Map<String, Object>) map.values().iterator().next()
                        : map)
                .orElse(Collections.emptyMap());

        Map<String, CredentialDisplayResponseDto> convertedClaimsMap = rawClaims.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> objectMapper.convertValue(entry.getValue(), CredentialDisplayResponseDto.class)
                ));

        if (convertedClaimsMap.isEmpty()) {
            log.warn("No display configuration found for SD-JWT format");
        }

        String resolvedLocale = LocaleUtils.resolveLocaleWithFallback(convertedClaimsMap, userLocale);
        LinkedHashMap<String, CredentialIssuerDisplayResponse> localizedDisplayMap = new LinkedHashMap<>();

        if (resolvedLocale != null) {
            convertedClaimsMap.forEach((key, dto) -> {
                dto.getDisplay().stream()
                        .filter(display -> LocaleUtils.matchesLocale(display.getLocale(), resolvedLocale))
                        .findFirst()
                        .ifPresent(display -> localizedDisplayMap.put(key, display));
            });
        }

        // Start with ordered fields
        Set<String> orderedKeys = Optional.ofNullable(credentialsSupportedResponse.getOrder())
                .map(LinkedHashSet::new) // preserve order
                .orElse(new LinkedHashSet<>());

        // Add remaining keys from credentialProperties that are not already in orderedKeys
        for (String key : credentialProperties.keySet()) {
            orderedKeys.add(key); // Set ensures no duplicates
        }

        for (String key : orderedKeys) {
            Object value = credentialProperties.get(key);
            if (value == null) {
                continue; // Skip fields without a value
            }

            CredentialIssuerDisplayResponse display = localizedDisplayMap.get(key);

            // Fallback if not found in metadata
            if (display == null) {
                display = new CredentialIssuerDisplayResponse();
                display.setName(camelToTitleCase(key));
                display.setLocale("en");
            }

            displayProperties.put(key, Map.of(display, value));
        }

        return displayProperties;
    }

    public Map<String, Object> extractClaimsFromSdJwt(String sdJwtString) {
        try {
            SDJWT sdJwt = SDJWT.parse(sdJwtString);
            Map<String, Object> claims = new HashMap<>();

            // Parse JWT payload
            String credentialJwt = sdJwt.getCredentialJwt();
            if (credentialJwt != null) {
                Map<String, Object> jwtPayload = parseJwtPayload(credentialJwt);
                if (jwtPayload != null) {
                    claims.putAll(jwtPayload);
                }
            }

            // Create disclosure map for efficient lookup
            Map<String, Disclosure> disclosureMap = createDisclosureMap(sdJwt.getDisclosures());

            // Process nested claims and arrays recursively
            processNestedClaims(claims, disclosureMap);

            // Remove standard JWT claims and SD-JWT metadata
            List<String> metadataKeys = Arrays.asList("vct", "cnf", "iss", "sub", "aud", "exp", "nbf", "iat", "jti");
            metadataKeys.forEach(claims::remove);

            // Clean up remaining SD-JWT specific keys recursively
            cleanupSDJWTKeys(claims);

            return claims;

        } catch (IllegalArgumentException e) {
            log.error("Error parsing SD-JWT with Authlete library: {}", e.getMessage(), e);
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("Unexpected error processing SD-JWT", e);
            return Collections.emptyMap();
        }
    }

    private Map<String, Disclosure> createDisclosureMap(List<Disclosure> disclosures) {
        Map<String, Disclosure> disclosureMap = new HashMap<>();
        if (disclosures != null && !disclosures.isEmpty()) {
            for (Disclosure disclosure : disclosures) {
                try {
                    // Get the hash/digest for this disclosure
                    String hash = getDisclosureHash(disclosure);
                    if (hash != null) {
                        disclosureMap.put(hash, disclosure);
                    }
                } catch (Exception e) {
                    log.warn("Failed to process disclosure for mapping: {}", e.getMessage());
                }
            }
        }
        return disclosureMap;
    }

    private String getDisclosureHash(Disclosure disclosure) {
        try {
            // Try to get the digest from the disclosure object
            // This depends on your SD-JWT library implementation
            if (disclosure instanceof com.authlete.sd.Disclosure) {
                return ((com.authlete.sd.Disclosure) disclosure).digest("sha-256");
            }

            // Alternative: if your library has a different method
            // return disclosure.getDigest();
            // or calculate it manually if needed

            return null;
        } catch (Exception e) {
            log.warn("Could not get disclosure hash: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void processNestedClaims(Object current, Map<String, Disclosure> disclosureMap) {
        if (current instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) current;
            processMapClaims(map, disclosureMap);
        } else if (current instanceof List) {
            List<Object> list = (List<Object>) current;
            processListClaims(list, disclosureMap);
        }
    }

    @SuppressWarnings("unchecked")
    private void processMapClaims(Map<String, Object> map, Map<String, Disclosure> disclosureMap) {
        // Process _sd array if present
        Object sdValue = map.get(SD_DIGEST_KEY);
        if (sdValue instanceof List) {
            List<String> sdDigests = (List<String>) sdValue;

            // Create a list to track successfully processed digests
            List<String> processedDigests = new ArrayList<>();

            for (String digest : sdDigests) {
                if (digest != null && DIGEST_PATTERN.matcher(digest).matches()) {
                    Disclosure disclosure = disclosureMap.get(digest);
                    if (disclosure != null) {
                        try {
                            String claimName = disclosure.getClaimName();
                            Object claimValue = disclosure.getClaimValue();

                            if (claimName != null && claimValue != null) {
                                // Handle nested path claims (e.g., "address.street")
                                if (claimName.contains(".")) {
                                    setNestedValue(map, claimName, claimValue);
                                } else {
                                    map.put(claimName, claimValue);
                                }

                                // Recursively process the disclosed value
                                processNestedClaims(claimValue, disclosureMap);
                                processedDigests.add(digest);
                            }
                        } catch (Exception e) {
                            log.warn("Failed to process disclosure for digest {}: {}", digest, e.getMessage());
                        }
                    }
                }
            }
        }

        // Process nested objects and arrays recursively
        // Create a copy of entries to avoid ConcurrentModificationException
        Set<Map.Entry<String, Object>> entries = new HashSet<>(map.entrySet());
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            if (!SD_DIGEST_KEY.equals(key) && !SD_ALG_KEY.equals(key)) {
                processNestedClaims(entry.getValue(), disclosureMap);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processListClaims(List<Object> list, Map<String, Disclosure> disclosureMap) {
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);

            // Check if this is a digest that should be replaced with disclosed content
            if (item instanceof String) {
                String itemStr = (String) item;
                if (DIGEST_PATTERN.matcher(itemStr).matches()) {
                    Disclosure disclosure = disclosureMap.get(itemStr);
                    if (disclosure != null) {
                        try {
                            // For array elements, the disclosure typically contains just the value
                            Object disclosedValue = disclosure.getClaimValue();
                            if (disclosedValue != null) {
                                list.set(i, disclosedValue);
                                // Recursively process the disclosed value
                                processNestedClaims(disclosedValue, disclosureMap);
                            }
                        } catch (Exception e) {
                            log.warn("Failed to process array element disclosure: {}", e.getMessage());
                        }
                    }
                }
            } else {
                // Recursively process non-digest items
                processNestedClaims(item, disclosureMap);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setNestedValue(Map<String, Object> root, String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = root;

        // Navigate to the parent of the target location
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Object next = current.get(part);

            if (!(next instanceof Map)) {
                // Create intermediate map if it doesn't exist
                next = new HashMap<String, Object>();
                current.put(part, next);
            }
            current = (Map<String, Object>) next;
        }

        // Set the final value
        current.put(parts[parts.length - 1], value);
    }

    @SuppressWarnings("unchecked")
    private void cleanupSDJWTKeys(Map<String, Object> claims) {
        removeSDJWTKeysRecursively(claims);
    }

    @SuppressWarnings("unchecked")
    private void removeSDJWTKeysRecursively(Object current) {
        if (current instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) current;

            // Remove SD-JWT specific keys
            map.remove(SD_DIGEST_KEY);
            map.remove(SD_ALG_KEY);

            // Recursively clean nested structures
            for (Object value : map.values()) {
                removeSDJWTKeysRecursively(value);
            }
        } else if (current instanceof List) {
            List<Object> list = (List<Object>) current;
            for (Object item : list) {
                removeSDJWTKeysRecursively(item);
            }
        }
    }
}