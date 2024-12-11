package io.mosip.mimoto.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.dto.DataShareResponseDto;
import io.mosip.mimoto.dto.mimoto.VCCredentialResponse;
import io.mosip.mimoto.dto.openid.datashare.DataShareResponseWrapperDTO;
import io.mosip.mimoto.dto.openid.presentation.PresentationRequestDTO;
import io.mosip.mimoto.exception.InvalidCredentialResourceException;
import io.mosip.mimoto.exception.ErrorConstants;
import io.mosip.mimoto.util.RestApiClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.oro.text.regex.PatternMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

@Slf4j
@Service
public class DataShareServiceImpl {

    @Autowired
    RestApiClient restApiClient;

    @Value("${mosip.data.share.url}")
    String dataShareHostUrl;

    @Value("${mosip.data.share.create.url}")
    String dataShareCreateUrl;

    @Value("${mosip.data.share.get.url.pattern}")
    String dataShareGetUrlPattern;

    @Value("${mosip.data.share.create.retry.count}")
    Integer maxRetryCount;

    @Autowired
    ObjectMapper objectMapper;

    PathMatcher pathMatcher ;

    @PostConstruct
    public void setUp(){
        pathMatcher = new AntPathMatcher();
    }

    public String storeDataInDataShare(String data, String credentialValidity) throws Exception {
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
        DataShareResponseWrapperDTO dataShareResponseWrapperDTO = pushCredentialIntoDataShare(requestEntity, credentialValidity);
        log.info("Data pushed into DataShare -> " + dataShareResponseWrapperDTO);
        return  dataShareResponseWrapperDTO.getDataShare().getUrl();
    }

    private DataShareResponseWrapperDTO pushCredentialIntoDataShare(HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity, String credentialValidity) throws Exception {
        int attempt =0 ;
        DataShareResponseWrapperDTO dataShareResponseWrapperDTO = null;
        while(attempt++ < maxRetryCount ){
            try {
                dataShareResponseWrapperDTO = restApiClient.postApi(dataShareCreateUrl + "?usageCountForStandaloneMode=" + credentialValidity, MediaType.MULTIPART_FORM_DATA, requestEntity, DataShareResponseWrapperDTO.class);
            } catch (Exception e) {
                log.error(attempt + " attempt to push credential failed");
            }
        }
        if(dataShareResponseWrapperDTO == null){
            throw new InvalidCredentialResourceException(
                    ErrorConstants.REQUEST_TIMED_OUT.getErrorCode(),
                    ErrorConstants.REQUEST_TIMED_OUT.getErrorMessage());
        }
        return dataShareResponseWrapperDTO;
    }

    public  VCCredentialResponse downloadCredentialFromDataShare(PresentationRequestDTO presentationRequestDTO) throws JsonProcessingException {
        log.info("Started the Credential Download From DataShare");
        String credentialsResourceUri = presentationRequestDTO.getResource();
        if(!pathMatcher.match(dataShareGetUrlPattern, credentialsResourceUri)){
            throw new InvalidCredentialResourceException(
                    ErrorConstants.RESOURCE_INVALID.getErrorCode(),
                    ErrorConstants.RESOURCE_INVALID.getErrorMessage());
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.add(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String vcCredentialResponseString = restTemplate.exchange(credentialsResourceUri, HttpMethod.GET, entity, String.class).getBody();
        if (vcCredentialResponseString == null) {
            throw new InvalidCredentialResourceException(
                    ErrorConstants.SERVER_UNAVAILABLE.getErrorCode(),
                    ErrorConstants.SERVER_UNAVAILABLE.getErrorMessage());
        }
        VCCredentialResponse vcCredentialResponse = objectMapper.readValue(vcCredentialResponseString, VCCredentialResponse.class);
        log.info("Completed Mapping the Credential to Object => " + vcCredentialResponse );
        if(vcCredentialResponse.getCredential() == null){
            DataShareResponseDto dataShareResponse = objectMapper.readValue(vcCredentialResponseString, DataShareResponseDto.class);
            String errorCode = dataShareResponse.getErrors().get(0).getErrorCode();
            throw new InvalidCredentialResourceException(errorCode.equals("DAT-SER-008") ? ErrorConstants.RESOURCE_NOT_FOUND.getErrorMessage() : ErrorConstants.RESOURCE_EXPIRED.getErrorMessage());
        }
        return vcCredentialResponse;
    }

}
