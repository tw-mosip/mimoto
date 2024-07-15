package io.mosip.mimoto.service;

import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.IssuersDTO;
import io.mosip.mimoto.dto.mimoto.*;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.exception.InvalidIssuerIdException;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface IssuersService {
    IssuersDTO getAllIssuers(String search) throws ApiNotAccessibleException, IOException;

    IssuersDTO getAllIssuersWithAllFields() throws ApiNotAccessibleException, IOException;

    IssuerDTO getIssuerConfig(String issuerId) throws ApiNotAccessibleException, IOException, InvalidIssuerIdException;

    ResponseEntity<String>  getIssuersWellknown(String issuerId) throws ApiNotAccessibleException, IOException;
}
