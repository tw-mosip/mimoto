package io.mosip.mimoto.service;

import io.mosip.mimoto.dto.IssuerConfigDTO;
import io.mosip.mimoto.dto.IssuerMapDTO;

public interface IssuersService {
    IssuerMapDTO getAllIssuers();

    IssuerConfigDTO getIssuerConfig(String issuerId);
}
