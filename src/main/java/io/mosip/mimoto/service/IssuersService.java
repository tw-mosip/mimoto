package io.mosip.mimoto.service;

import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.IssuersDTO;

public interface IssuersService {
    IssuersDTO getAllIssuers();

    IssuerDTO getIssuerConfig(String issuerId);
}
