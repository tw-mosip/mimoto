package io.mosip.mimoto.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.dto.mimoto.VCCredentialProperties;
import io.mosip.mimoto.dto.openid.presentation.PresentationDefinitionDTO;
import io.mosip.mimoto.dto.openid.presentation.VerifiablePresentationDTO;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.exception.InvalidVerifierException;
import io.mosip.mimoto.service.impl.PresentationServiceImpl;
import io.mosip.mimoto.service.impl.VerifiersServiceImpl;
import io.mosip.mimoto.util.RestApiClient;
import io.mosip.mimoto.util.TestUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class PresentationServiceTest {
    @Mock
    VerifiersService verifiersService = new VerifiersServiceImpl();
    @Mock
    RestApiClient restApiClient;
    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    PresentationServiceImpl presentationService;

    @Before
    public void setup() throws JsonProcessingException {
        ReflectionTestUtils.setField(presentationService, "injiVerifyRedirectUrl", "%s#vp_token=%s&presentation_submission=%s");
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(PresentationDefinitionDTO.class))).thenReturn(TestUtilities.getPresentationDefinitionDTO());
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(VerifiablePresentationDTO.class))).thenReturn(TestUtilities.getVerifiablePresentationDTO());
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("test-data");
    }
    @Test(expected = InvalidVerifierException.class)
    public void throwInvalidVerifierExceptionWhenClientIdPassedIsIncorrect() throws ApiNotAccessibleException, IOException {
        doThrow(InvalidVerifierException.class).when(verifiersService).validateVerifier(Mockito.any());
        presentationService.authorizePresentation(TestUtilities.getPresentationRequestDTO());
    }

    @Test
    public void credentialProofMatchingWithVPRequest() throws Exception {
        ObjectMapper realObjectMapper = new ObjectMapper();
        String mockResponse = realObjectMapper.writeValueAsString(TestUtilities.getVCCredentialPropertiesDTO("Ed25519Signature2020"));

        doNothing().when(verifiersService).validateVerifier(Mockito.any());
        Mockito.when(restApiClient.getApi(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(mockResponse);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(VCCredentialProperties.class))).thenReturn(TestUtilities.getVCCredentialPropertiesDTO("Ed25519Signature2020"));

        String actualRedirectUrl = presentationService.authorizePresentation(TestUtilities.getPresentationRequestDTO());
        String expectedRedirectUrl = "test_redirect_uri#vp_token=dGVzdC1kYXRh&presentation_submission=dGVzdC1kYXRh";

        Assert.assertEquals(actualRedirectUrl, expectedRedirectUrl);
    }

    @Test
    public void credentialProofMismatchWithVPRequest() throws ApiNotAccessibleException, IOException {
        ObjectMapper realObjectMapper = new ObjectMapper();
        String mockResponse = realObjectMapper.writeValueAsString(TestUtilities.getVCCredentialPropertiesDTO("RSASignature2020"));

        doNothing().when(verifiersService).validateVerifier(Mockito.any());
        Mockito.when(restApiClient.getApi(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(mockResponse);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(VCCredentialProperties.class))).thenReturn(TestUtilities.getVCCredentialPropertiesDTO("RSASignature2020"));

        String actualRedirectUrl = presentationService.authorizePresentation(TestUtilities.getPresentationRequestDTO());
        Assert.assertNull(actualRedirectUrl);
    }
}
