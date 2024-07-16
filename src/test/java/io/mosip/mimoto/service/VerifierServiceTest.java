package io.mosip.mimoto.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mosip.mimoto.dto.openid.VerifierDTO;
import io.mosip.mimoto.dto.openid.presentation.PresentationRequestDTO;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.exception.InvalidVerifierException;
import io.mosip.mimoto.service.impl.VerifiersServiceImpl;
import io.mosip.mimoto.util.TestUtilities;
import io.mosip.mimoto.util.Utilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class VerifierServiceTest {

    @Mock
    Utilities utilities;
    @InjectMocks
    VerifiersServiceImpl verifiersService;

    @Before
    public void setUp() throws JsonProcessingException {
        when(utilities.getTrustedVerifiersJsonValue()).thenReturn(TestUtilities.getTrustedVerifiers());
    }

    @Test
    public void getCorrectVerifierWhenCorrectClientIdIsPassed() throws ApiNotAccessibleException, IOException {

        Optional<VerifierDTO> verifierDTO = verifiersService.getVerifiersByClientId("test-clientId");
        Assert.assertNotNull(verifierDTO.get());
        Assert.assertEquals(verifierDTO.get().getClientId(), "test-clientId");
    }

    @Test
    public void getNullWhenInvalidClientIdIsPassed() throws ApiNotAccessibleException, IOException {
        Optional<VerifierDTO> verifierDTO = verifiersService.getVerifiersByClientId("test-clientId2");
        Assert.assertTrue(verifierDTO.isEmpty());
    }

    @Test
    public void validateTrustedVerifiersAndDoNothing() throws ApiNotAccessibleException, IOException {
        PresentationRequestDTO presentationRequestDTO = new PresentationRequestDTO();
        presentationRequestDTO.setClient_id("test-clientId");
        presentationRequestDTO.setRedirect_uri("test-redirectUri");
        verifiersService.validateVerifier(presentationRequestDTO);
    }

    @Test(expected = InvalidVerifierException.class)
    public void validateTrustedVerifiersAndThrowInvalidVerifierExceptionWhenClientIdIsIncorrect() throws ApiNotAccessibleException, IOException {
        PresentationRequestDTO presentationRequestDTO = new PresentationRequestDTO();
        presentationRequestDTO.setClient_id("test-clientId2");
        presentationRequestDTO.setRedirect_uri("test-redirectUri");
        verifiersService.validateVerifier(presentationRequestDTO);
    }

    @Test(expected = InvalidVerifierException.class)
    public void validateTrustedVerifiersAndThrowInvalidVerifiersExceptionWhenRedirectUriIsIncorrect() throws ApiNotAccessibleException, IOException {
        PresentationRequestDTO presentationRequestDTO = new PresentationRequestDTO();
        presentationRequestDTO.setClient_id("test-clientId");
        presentationRequestDTO.setRedirect_uri("test-redirectUri2");
        verifiersService.validateVerifier(presentationRequestDTO);
    }
}
