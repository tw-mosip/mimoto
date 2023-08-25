package io.mosip.mimoto.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.IssuersDTO;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.service.IssuersService;
import io.mosip.mimoto.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class IssuersServiceImpl implements IssuersService {
    @Autowired
    private Utilities utilities;

    private final Logger logger = LoggerFactory.getLogger(IssuersServiceImpl.class);

    @Override
    public IssuersDTO getAllIssuers() throws ApiNotAccessibleException, IOException {
        IssuersDTO issuers = new IssuersDTO();
        try {
            String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
            if (issuersConfigJsonValue == null) {
                throw new ApiNotAccessibleException();
            }
            Gson gsonWithIssuerDataOnlyFilter = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            issuers = gsonWithIssuerDataOnlyFilter.fromJson(issuersConfigJsonValue, IssuersDTO.class);
        } catch (IOException | ApiNotAccessibleException exception) {
            logger.error(exception.getMessage());
            throw exception;
        }

        return issuers;
    }


    @Override
    public IssuerDTO getIssuerConfig(String issuerId) throws ApiNotAccessibleException, IOException {
        IssuerDTO issuerDTO = null;
        try {
            String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
            if (issuersConfigJsonValue == null) {
                throw new ApiNotAccessibleException();
            }
            IssuersDTO issuers = new Gson().fromJson(issuersConfigJsonValue, IssuersDTO.class);
            Optional<IssuerDTO> issuerConfigResp = issuers.getIssuers().stream()
                    .filter(issuer -> issuer.getId().equals(issuerId))
                    .findFirst();
            if (issuerConfigResp.isPresent())
                issuerDTO = issuerConfigResp.get();
        } catch (IOException | ApiNotAccessibleException exception) {
            logger.error(exception.getMessage());
            throw exception;
        }
        return issuerDTO;
    }
}