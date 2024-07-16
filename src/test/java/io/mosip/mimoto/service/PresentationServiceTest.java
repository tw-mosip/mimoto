package io.mosip.mimoto.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.dto.openid.presentation.PresentationSubmissionDTO;
import io.mosip.mimoto.dto.openid.presentation.VerifiablePresentationDTO;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.exception.InvalidVerifierException;
import io.mosip.mimoto.service.impl.PresentationServiceImpl;
import io.mosip.mimoto.service.impl.VerifiersServiceImpl;
import io.mosip.mimoto.util.TestUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class PresentationServiceTest {
    @Mock
    VerifiersService verifiersService = new VerifiersServiceImpl();

    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    PresentationServiceImpl presentationService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(presentationService, "injiVerifyRedirectUrl", "%s#vp_token=%s&presentation_submission=presentation_submission");
    }

    @Test(expected = InvalidVerifierException.class)
    public void throwInvalidVerifierExceptionWhenClientIdPassedIsIncorrect() throws ApiNotAccessibleException, IOException {
        doThrow(InvalidVerifierException.class).when(verifiersService).validateVerifier(Mockito.any());
        presentationService.authorizePresentation(TestUtilities.getPresentationRequestDTO());
    }

    @Test
    public void credentialProofMatchingWithVPRequest() throws ApiNotAccessibleException, IOException {
        doNothing().when(verifiersService).validateVerifier(Mockito.any());
        ObjectMapper objectMapper = new ObjectMapper();
        String mockResponse = objectMapper.writeValueAsString(TestUtilities.getVCCredentialPropertiesDTO("Ed25519Signature2020"));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(responseEntity);
        String actualRedirectUrl = presentationService.authorizePresentation(TestUtilities.getPresentationRequestDTO()).split("&presentation_submission")[0];

        VerifiablePresentationDTO verifiablePresentationDTO = TestUtilities.getVerifiablePresentationDTO();
        String expectedRedirectUrl = "test_redirect_uri#vp_token=" + Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(verifiablePresentationDTO));
        Assert.assertEquals(actualRedirectUrl, expectedRedirectUrl);
    }

    @Test
    public void credentialProofMismatchWithVPRequest() throws ApiNotAccessibleException, IOException {
        doNothing().when(verifiersService).validateVerifier(Mockito.any());
        ObjectMapper objectMapper = new ObjectMapper();
        String mockResponse = objectMapper.writeValueAsString(TestUtilities.getVCCredentialPropertiesDTO("RSASignature2020"));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(responseEntity);
        String actualRedirectUrl = presentationService.authorizePresentation(TestUtilities.getPresentationRequestDTO());
        Assert.assertNull(actualRedirectUrl);
    }

    @Test
    public void constructVerifiablePresentationWithCredential() throws JsonProcessingException {
        String actualPresentation = PresentationServiceImpl.constructVerifiablePresentationString(TestUtilities.getVCCredentialPropertiesDTO("Ed25519Signature2020"));
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedPresentation = objectMapper.writeValueAsString(TestUtilities.getVerifiablePresentationDTO());
        Assert.assertEquals(actualPresentation, expectedPresentation);
    }

    @Test
    public void constructPresentationSubmissionForVP() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedPresentation = objectMapper.writeValueAsString(TestUtilities.getVerifiablePresentationDTO());
        String actualPresentation = PresentationServiceImpl.constructPresentationSubmission(expectedPresentation);
        PresentationSubmissionDTO presentationSubmissionDTO = objectMapper.readValue(actualPresentation, PresentationSubmissionDTO.class);
        Assert.assertNotNull(presentationSubmissionDTO.getId());
        Assert.assertNotNull(presentationSubmissionDTO.getDefinition_id());
        Assert.assertNotNull(presentationSubmissionDTO.getDescriptorMap());
        Assert.assertEquals(presentationSubmissionDTO.getDescriptorMap().size(), 1);
    }
}
