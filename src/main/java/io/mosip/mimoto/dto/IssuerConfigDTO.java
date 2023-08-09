package io.mosip.mimoto.dto;

import lombok.Data;

import java.util.Map;


@Data
class ServiceConfiguration {
    String authorizationEndpoint;
    String tokenEndpoint;
    String revocationEndpoint;
}

@Data
public class IssuerConfigDTO {
    String id;
    String displayName;
    String logoUrl;
    String clientId;
    String wellKnownEndpoint;
    String redirectionUri;
    ServiceConfiguration serviceConfiguration;
    Map<String, String> additionalHeaders;
}
