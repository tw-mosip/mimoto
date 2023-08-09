package io.mosip.mimoto.dto;

import lombok.Data;

import java.util.Map;


@Data
public class Issuers {
    Map<String, IssuerDTO> issuers;
}
