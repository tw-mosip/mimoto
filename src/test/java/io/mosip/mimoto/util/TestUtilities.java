package io.mosip.mimoto.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.dto.DisplayDTO;
import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.LogoDTO;
import io.mosip.mimoto.dto.mimoto.*;
import io.mosip.mimoto.dto.openid.VerifierDTO;
import io.mosip.mimoto.dto.openid.VerifiersDTO;
import io.mosip.mimoto.dto.openid.presentation.*;

import java.util.*;

public class TestUtilities {
    public static CredentialsSupportedResponse getCredentialSupportedResponse(String credentialSupportedName){
        LogoDTO logo = new LogoDTO();
        logo.setUrl("/logo");
        logo.setAlt_text("logo-url");
        CredentialSupportedDisplayResponse credentialSupportedDisplay = new CredentialSupportedDisplayResponse();
        credentialSupportedDisplay.setLogo(logo);
        credentialSupportedDisplay.setName(credentialSupportedName);
        credentialSupportedDisplay.setLocale("en");
        credentialSupportedDisplay.setTextColor("#FFFFFF");
        credentialSupportedDisplay.setBackgroundColor("#B34622");
        CredentialIssuerDisplayResponse credentialIssuerDisplayResponse = new CredentialIssuerDisplayResponse();
        credentialIssuerDisplayResponse.setName("Given Name");
        credentialIssuerDisplayResponse.setLocale("en");
        CredentialDisplayResponseDto credentialDisplayResponseDto = new CredentialDisplayResponseDto();
        credentialDisplayResponseDto.setDisplay(Collections.singletonList(credentialIssuerDisplayResponse));
        CredentialDefinitionResponseDto credentialDefinitionResponseDto = new CredentialDefinitionResponseDto();
        credentialDefinitionResponseDto.setType(List.of("VerifiableCredential", credentialSupportedName));
        credentialDefinitionResponseDto.setCredentialSubject(Map.of("name", credentialDisplayResponseDto));
        CredentialsSupportedResponse credentialsSupportedResponse = new CredentialsSupportedResponse();
        credentialsSupportedResponse.setFormat("ldp_vc");
        credentialsSupportedResponse.setId(credentialSupportedName+"id");
        credentialsSupportedResponse.setScope(credentialSupportedName+"_vc_ldp");
        credentialsSupportedResponse.setDisplay(Collections.singletonList(credentialSupportedDisplay));
        credentialsSupportedResponse.setProofTypesSupported(Collections.singletonList("jwt"));
        credentialsSupportedResponse.setCredentialDefinition(credentialDefinitionResponseDto);
        return credentialsSupportedResponse;
    }

    public static CredentialIssuerWellKnownResponse getCredentialIssuerWellKnownResponseDto(String issuerName, List<CredentialsSupportedResponse> credentialsSupportedResponses){
        CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = new CredentialIssuerWellKnownResponse();
        credentialIssuerWellKnownResponse.setCredentialIssuer(issuerName);
        credentialIssuerWellKnownResponse.setCredentialEndPoint("/credential_endpoint");
        credentialIssuerWellKnownResponse.setCredentialsSupported(credentialsSupportedResponses);
        return credentialIssuerWellKnownResponse;
    }
    public static IssuerDTO getIssuerDTO(String issuerName) {
        LogoDTO logo = new LogoDTO();
        logo.setUrl("/logo");
        logo.setAlt_text("logo-url");
        DisplayDTO display = new DisplayDTO();
        display.setName(issuerName);
        display.setTitle("Download via " + issuerName);
        display.setDescription(issuerName + " description");
        display.setLanguage("en");
        display.setLogo(logo);
        IssuerDTO issuer = new IssuerDTO();
        issuer.setCredential_issuer(issuerName + "id");
        issuer.setDisplay(Collections.singletonList(display));
        issuer.setClient_id("123");
        if (issuerName.equals("Issuer1")) issuer.setWellKnownEndpoint("/.well-known");
        else {
            issuer.setRedirect_uri(null);
            issuer.setAuthorization_endpoint(null);
            issuer.setCredential_endpoint(null);
            issuer.setToken_endpoint(null);
            issuer.setScopes_supported(null);
        }
        return issuer;
    }

    public static IssuerDTO getIssuerConfigDTO(String issuerName, List<String> nullFields) {
        LogoDTO logo = new LogoDTO();
        logo.setUrl("/logo");
        logo.setAlt_text("logo-url");
        DisplayDTO display = new DisplayDTO();
        display.setName(issuerName);
        display.setTitle("Download via " + issuerName);
        display.setDescription(issuerName + " description");
        display.setLanguage("en");
        display.setLogo(logo);
        IssuerDTO issuer = new IssuerDTO();
        issuer.setCredential_issuer(issuerName + "id");
        issuer.setDisplay(Collections.singletonList(display));
        issuer.setClient_id("123");
        issuer.setEnabled("true");
        if (issuerName.equals("Issuer1")) issuer.setWellKnownEndpoint("/.well-known");
        else {
            if (!nullFields.contains("redirect_uri"))
                issuer.setRedirect_uri("/redirection");
            if (!nullFields.contains("authorization_audience"))
                issuer.setAuthorization_audience("/authorization_audience");
            if (!nullFields.contains("redirect_uri"))
                issuer.setRedirect_uri("/redirection");
            if (!nullFields.contains("authorization_endpoint"))
                issuer.setAuthorization_endpoint("/authorization_endpoint");
            if (!nullFields.contains("token_endpoint"))
                issuer.setToken_endpoint("/token_endpoint");
            if (!nullFields.contains("proxy_token_endpoint"))
                issuer.setProxy_token_endpoint("/proxy_token_endpoint");
            if (!nullFields.contains("credential_endpoint"))
                issuer.setCredential_endpoint("/credential_endpoint");
            if (!nullFields.contains("credential_audience"))
                issuer.setCredential_audience("/credential_audience");
            if (!nullFields.contains("additional_headers"))
                issuer.setAdditional_headers(Map.of("Content-Type", "application/json"));
        }
        return issuer;
    }

    public static PresentationRequestDTO getPresentationRequestDTO(){
        PresentationRequestDTO presentationRequestDTO = new PresentationRequestDTO();
        presentationRequestDTO.setClient_id("test_client_id");
        presentationRequestDTO.setResource("test_resource");
        presentationRequestDTO.setResponse_type("test_response_type");
        presentationRequestDTO.setRedirect_uri("test_redirect_uri");
        presentationRequestDTO.setPresentation_definition("{\"id\":\"vp token example\",\"input_descriptors\":[{\"id\":\"id card credential\",\"format\":{\"ldpVc\":{\"proofTypes\":[\"Ed25519Signature2020\"]}}}]}");
        return presentationRequestDTO;
    }

    public static VCCredentialProperties getVCCredentialPropertiesDTO(String type){
        VCCredentialProperties vcCredentialProperties = new VCCredentialProperties();
        vcCredentialProperties.setId("test-id");
        vcCredentialProperties.setIssuer("test-issuer");
        vcCredentialProperties.setIssuanceDate("test-issuanceDate");
        vcCredentialProperties.setExpirationDate("test-expirationDate");

        ArrayList<String> contextList = new ArrayList<>();
        contextList.add("context-1");
        contextList.add("context-2");
        vcCredentialProperties.setContext(contextList);

        List<String> typeList = new ArrayList<>();
        typeList.add("VerifiableCredential");
        typeList.add("VCTypeCredential");
        vcCredentialProperties.setContext(typeList);

        VCCredentialResponseProof  vcCredentialResponseProof = new VCCredentialResponseProof();
        vcCredentialResponseProof.setType(type);
        vcCredentialResponseProof.setProofPurpose("test-proofPurpose");
        vcCredentialResponseProof.setProofValue("test-proofValue");
        vcCredentialResponseProof.setJws("test-jws");
        vcCredentialResponseProof.setVerificationMethod("test-verificationMethod");
        vcCredentialProperties.setProof(vcCredentialResponseProof);

        Map<String, Object> credentialSubject = new HashMap<>();
        credentialSubject.put("key1", "value1");
        credentialSubject.put("key2", "value2");
        vcCredentialProperties.setCredentialSubject(credentialSubject);

        return vcCredentialProperties;
    }

    public static VCCredentialResponse getVCCredentialResponseDTO(String type){
        VCCredentialResponse vcCredentialResponse = new VCCredentialResponse();
        VCCredentialProperties vcCredentialProperties = getVCCredentialPropertiesDTO(type);
        vcCredentialResponse.setCredential(vcCredentialProperties);
        vcCredentialResponse.setFormat("ldp_vc");
        return vcCredentialResponse;
    }

    public static PresentationDefinitionDTO getPresentationDefinitionDTO(){
        PresentationDefinitionDTO presentationDefinitionDTO = new PresentationDefinitionDTO();
        InputDescriptorDTO inputDescriptorDTO = new InputDescriptorDTO();
        Format format = new Format();
        LDPVc ldpVc = new LDPVc();
        ldpVc.setProofTypes(Collections.singletonList("Ed25519Signature2020"));
        format.setLdpVc(ldpVc);
        inputDescriptorDTO.setId("test-input-id");
        inputDescriptorDTO.setFormat(format);
        presentationDefinitionDTO.setId("test-id");
        presentationDefinitionDTO.setInputDescriptors(Collections.singletonList(inputDescriptorDTO));
        return presentationDefinitionDTO;
    }

    public static VerifiablePresentationDTO getVerifiablePresentationDTO(){
        VerifiablePresentationDTO verifiablePresentationDTO = new VerifiablePresentationDTO();

        ArrayList<String> contextList = new ArrayList<>();
        contextList.add("https://www.w3.org/2018/credentials/v1");
        verifiablePresentationDTO.setContext(contextList);

        List<String> typeList = new ArrayList<>();
        typeList.add("VerifiablePresentation");
        verifiablePresentationDTO.setType(typeList);

        verifiablePresentationDTO.setVerifiableCredential(Collections.singletonList(getVCCredentialPropertiesDTO("Ed25519Signature2020")));
        return verifiablePresentationDTO;
    }

    public static VerifiersDTO getTrustedVerifiers() throws JsonProcessingException {

        VerifiersDTO verifiersDTO = new VerifiersDTO();
        VerifierDTO verifierDTO = new VerifierDTO();
        verifierDTO.setClientId("test-clientId");
        verifierDTO.setRedirectUri(Collections.singletonList("test-redirectUri"));
        verifiersDTO.setVerifiers(Collections.singletonList(verifierDTO));
        return verifiersDTO;
    }

    public static String getObjectAsString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
