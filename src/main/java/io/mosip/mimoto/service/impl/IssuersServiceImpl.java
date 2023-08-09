package io.mosip.mimoto.service.impl;

import com.google.gson.Gson;
import io.mosip.mimoto.dto.IssuerConfigDTO;
import io.mosip.mimoto.dto.IssuerConfigMapDTO;
import io.mosip.mimoto.dto.IssuerMapDTO;
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
    private final Gson gson = new Gson();

    @Autowired
    private Utilities utilities;

    private final Logger logger = LoggerFactory.getLogger(IssuersServiceImpl.class);

    @Override
    public IssuerMapDTO getAllIssuers() {
        IssuerMapDTO issuers = new IssuerMapDTO();
        try {
            String issuersConfigJsonValue = utilities.getIssuersConfigJsonValue();
            logger.info("Issuers Config String - > {}", issuersConfigJsonValue);
            issuers = gson.fromJson(issuersConfigJsonValue, IssuerMapDTO.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return issuers;
    }


    @Override
    public IssuerConfigDTO getIssuerConfig(String issuerId) {
        IssuerConfigDTO issuerConfigDTO = new IssuerConfigDTO();
        try {
            IssuerConfigMapDTO issuers = gson.fromJson(utilities.getIssuersConfigJsonValue(), IssuerConfigMapDTO.class);
             Optional<IssuerConfigDTO> issuerConfigResp = issuers.getIssuers().stream()
                    .filter(issuer -> issuer.getId().equals(issuerId))
                            .findFirst();
            return issuerConfigResp.orElseGet(IssuerConfigDTO::new);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return issuerConfigDTO;
    }
}
