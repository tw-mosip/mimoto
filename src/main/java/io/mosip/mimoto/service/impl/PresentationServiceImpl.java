package io.mosip.mimoto.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.dto.mimoto.VCCredentialProperties;
import io.mosip.mimoto.dto.mimoto.VCCredentialResponse;
import io.mosip.mimoto.dto.openid.presentation.*;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.service.PresentationService;
import io.mosip.mimoto.service.VerifiersService;
import io.mosip.mimoto.util.RestApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PresentationServiceImpl implements PresentationService {

    @Autowired
    VerifiersService verifiersService;

    @Autowired
    RestApiClient restApiClient;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${mosip.inji.verify.redirect.url}")
    String injiVerifyRedirectUrl;

    private final Logger logger = LoggerFactory.getLogger(PresentationServiceImpl.class);

    @Override
    public String authorizePresentation(PresentationRequestDTO presentationRequestDTO) throws ApiNotAccessibleException, IOException {

        logger.info("Started the presentation Validation");
        verifiersService.validateVerifier(presentationRequestDTO);

        logger.info("Started the Credential Download From DataShare");
        String credentials_uri = presentationRequestDTO.getResource();
        String  vcCredentialResponseString = restApiClient.getApi(credentials_uri, String.class);

        logger.info("Started the ObjectMapping");
        VCCredentialResponse vcCredentialResponse = objectMapper.readValue(vcCredentialResponseString, VCCredentialResponse.class);
        PresentationDefinitionDTO presentationDefinitionDTO = objectMapper.readValue(presentationRequestDTO.getPresentation_definition(), PresentationDefinitionDTO.class);

        if(presentationDefinitionDTO.getInputDescriptors().get(0).getFormat().getLdpVc().getProofTypes().get(0).equals(vcCredentialResponse.getCredential().getProof().getType())){
            logger.info("Started the Construction of VP token");
            String vpToken = constructVerifiablePresentationString(vcCredentialResponse.getCredential());
            String presentationSubmission =constructPresentationSubmission(vpToken);
            return String.format(injiVerifyRedirectUrl,
                    presentationRequestDTO.getRedirect_uri(),
                    Base64.getUrlEncoder().encodeToString(vpToken.getBytes(StandardCharsets.UTF_8)),
                    Base64.getUrlEncoder().encodeToString(presentationSubmission.getBytes(StandardCharsets.UTF_8)));
        }
        return null;

    }

    private String constructVerifiablePresentationString(VCCredentialProperties vcCredentialProperties) throws JsonProcessingException {
        VerifiablePresentationDTO verifiablePresentationDTO = new VerifiablePresentationDTO();
        verifiablePresentationDTO.setVerifiableCredential(Collections.singletonList(vcCredentialProperties));
        verifiablePresentationDTO.setType(Collections.singletonList("VerifiablePresentation"));
        verifiablePresentationDTO.setContext(Collections.singletonList("https://www.w3.org/2018/credentials/v1"));
        return objectMapper.writeValueAsString(verifiablePresentationDTO);
    }

    private String constructPresentationSubmission(String vpToken) throws JsonProcessingException {
        VerifiablePresentationDTO verifiablePresentationDTO = objectMapper.readValue(vpToken, VerifiablePresentationDTO.class);
        PresentationSubmissionDTO presentationSubmissionDTO = new PresentationSubmissionDTO();
        List<SubmissionDescriptorDTO> submissionDescriptorDTOList = new ArrayList<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        verifiablePresentationDTO.getVerifiableCredential().forEach(verifiableCredential -> {
            SubmissionDescriptorDTO submissionDescriptorDTO = new SubmissionDescriptorDTO();
            submissionDescriptorDTO.setId(UUID.randomUUID().toString());
            submissionDescriptorDTO.setFormat("ldp_vc");
            submissionDescriptorDTO.setPath("$.verifiableCredential[" + atomicInteger.getAndIncrement() + "]");
            submissionDescriptorDTOList.add(submissionDescriptorDTO);
        });
        presentationSubmissionDTO.setId(UUID.randomUUID().toString());
        presentationSubmissionDTO.setDefinition_id(UUID.randomUUID().toString());
        presentationSubmissionDTO.setDescriptorMap(submissionDescriptorDTOList);
        return objectMapper.writeValueAsString(presentationSubmissionDTO);
    }

}
