package io.mosip.mimoto.dto;

import lombok.Data;

@Data
public class IssuerDTO {
    String id;
    String displayName;
    String logoUrl;
    String clientId;
    String wellKnownEndpoint;
}
