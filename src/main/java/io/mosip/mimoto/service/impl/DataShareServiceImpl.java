package io.mosip.mimoto.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.dto.mimoto.VCCredentialResponse;
import io.mosip.mimoto.dto.openid.datashare.DataShareResponseWrapperDTO;
import io.mosip.mimoto.dto.openid.presentation.PresentationRequestDTO;
import io.mosip.mimoto.exception.InvalidCredentialResourceException;
import io.mosip.mimoto.exception.OpenIdErrorMessages;
import io.mosip.mimoto.util.RestApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.net.URL;

@Service
public class DataShareServiceImpl {

    @Autowired
    RestApiClient restApiClient;

    @Value("${public.url}")
    String publicHostUrl;

    @Value("${mosip.data.share.url}")
    String dataShareUrl;

    private final Logger logger = LoggerFactory.getLogger(DataShareServiceImpl.class);

    @Autowired
    ObjectMapper objectMapper;

    public String storeDataInDataShare(String data) throws Exception {
        ByteArrayResource contentsAsResource = new ByteArrayResource(data.getBytes()) {
            @Override
            public String getFilename() {
                return "credential_file";
            }
        };
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", contentsAsResource);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        DataShareResponseWrapperDTO dataShareResponseWrapperDTO = restApiClient.postApi(dataShareUrl, MediaType.MULTIPART_FORM_DATA, requestEntity, DataShareResponseWrapperDTO.class);
        URL dataShareUrl = new URL(dataShareResponseWrapperDTO.getDataShare().getUrl());
//        return publicHostUrl + dataShareUrl.getPath();
        return "https://api-internal.dev.mosip.net" + dataShareUrl.getPath();
    }

    public  VCCredentialResponse downloadCredentialFromDataShare(PresentationRequestDTO presentationRequestDTO) throws JsonProcessingException {
        logger.info("Started the Credential Download From DataShare");
        String credentialsResourceUri = presentationRequestDTO.getResource();
        String vcCredentialResponseString = restApiClient.getApi(credentialsResourceUri, String.class);
        if (vcCredentialResponseString == null) {
            throw new InvalidCredentialResourceException(OpenIdErrorMessages.INVALID_RESOURCE.getErrorMessage());
        }
        return  objectMapper.readValue(vcCredentialResponseString, VCCredentialResponse.class);
    }

}
