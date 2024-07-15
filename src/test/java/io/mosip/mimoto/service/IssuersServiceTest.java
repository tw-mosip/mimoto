package io.mosip.mimoto.service;

import com.google.gson.Gson;
import io.mosip.mimoto.controller.IssuersController;
import io.mosip.mimoto.dto.DisplayDTO;
import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.IssuersDTO;
import io.mosip.mimoto.dto.LogoDTO;
import io.mosip.mimoto.dto.mimoto.*;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.exception.InvalidIssuerIdException;
import io.mosip.mimoto.service.impl.CredentialServiceImpl;
import io.mosip.mimoto.service.impl.IssuersServiceImpl;
import io.mosip.mimoto.util.RestApiClient;
import io.mosip.mimoto.util.Utilities;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static io.mosip.mimoto.util.TestUtilities.getIssuerConfigDTO;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class IssuersServiceTest {

    @InjectMocks
    IssuersServiceImpl issuersService = new IssuersServiceImpl();

    @InjectMocks
    CredentialServiceImpl credentialService = new CredentialServiceImpl();

    @Mock
    Utilities utilities;

    @Mock
    RestTemplate restTemplate;

    List<String> issuerConfigRelatedFields = List.of("additional_headers", "authorization_endpoint","authorization_audience", "token_endpoint", "proxy_token_endpoint", "credential_endpoint", "credential_audience", "redirect_uri");


    @Before
    public void setUp() throws Exception {
        IssuersDTO issuers = new IssuersDTO();
        issuers.setIssuers(List.of(getIssuerConfigDTO("Issuer1", Collections.emptyList()), getIssuerConfigDTO("Issuer2", Collections.emptyList())));
        Mockito.when(utilities.getIssuersConfigJsonValue()).thenReturn(new Gson().toJson(issuers));
    }

    @Test
    public void shouldReturnIssuersWithIssuerConfigAsNull() throws ApiNotAccessibleException, IOException {
        IssuersDTO expectedIssuers = new IssuersDTO();
        List<IssuerDTO> issuers = new ArrayList<>(List.of(getIssuerConfigDTO("Issuer1", issuerConfigRelatedFields), getIssuerConfigDTO("Issuer2", issuerConfigRelatedFields)));
        expectedIssuers.setIssuers(issuers);

        IssuersDTO expectedFilteredIssuers = new IssuersDTO();
        List<IssuerDTO> filteredIssuersList = new ArrayList<>(List.of(getIssuerConfigDTO("Issuer1", issuerConfigRelatedFields)));
        expectedFilteredIssuers.setIssuers(filteredIssuersList);

        IssuersDTO allIssuers = issuersService.getAllIssuers(null);
        IssuersDTO filteredIssuers = issuersService.getAllIssuers("Issuer1");
        assertEquals(expectedIssuers, allIssuers);
        assertEquals(expectedFilteredIssuers, filteredIssuers);
    }

    @Test(expected = ApiNotAccessibleException.class)
    public void shouldThrowApiNotAccessibleExceptionWhenIssuersJsonStringIsNullForGettingAllIssuers() throws IOException, ApiNotAccessibleException {
        Mockito.when(utilities.getIssuersConfigJsonValue()).thenReturn(null);

        issuersService.getAllIssuers(null);
    }

    @Test
    public void shouldReturnIssuerDataAndConfigForTheIssuerIdIfExist() throws ApiNotAccessibleException, IOException, InvalidIssuerIdException {
        IssuerDTO expectedIssuer = getIssuerConfigDTO("Issuer1", issuerConfigRelatedFields);

        IssuerDTO issuer = issuersService.getIssuerConfig("Issuer1id");

        assertEquals(expectedIssuer, issuer);
    }

    @Test
    public void shouldReturnIssuerWellknownForTheIssuerIdIfExist() throws ApiNotAccessibleException, IOException {
        String issuerId = "test-issuer";
        String wellKnownUrl = "https://example.com/.well-known";
        String wellKnownResponse = "{\"issuer\":\"test-issuer\",\"authorization_endpoint\":\"https://example.com/auth\"}";
        ResponseEntity<String> wellKnownResponseEntity=restTemplate.getForEntity(wellKnownResponse, String.class);

        Mockito.when(restTemplate.getForEntity(wellKnownUrl, String.class))
                .thenReturn(new ResponseEntity<>(wellKnownResponse, HttpStatus.OK));

        ResponseEntity<String> resultResponseEntity=issuersService.getIssuersWellknown(issuerId);
        assertEquals(wellKnownResponse, resultResponseEntity);
        verify(restTemplate, times(1)).getForEntity(wellKnownUrl, String.class);
    }

    @Test
    public void shouldReturnIssuerDataAndConfigForAllIssuer() throws ApiNotAccessibleException, IOException {
        IssuersDTO expectedIssuers = new IssuersDTO();
        List<IssuerDTO> issuers = new ArrayList<>(List.of(getIssuerConfigDTO("Issuer1", new ArrayList<>()), getIssuerConfigDTO("Issuer2", new ArrayList<>())));
        expectedIssuers.setIssuers(issuers);

        IssuersDTO issuersDTO = issuersService.getAllIssuersWithAllFields();

        assertEquals(expectedIssuers, issuersDTO);
    }

    @Test(expected = InvalidIssuerIdException.class)
    public void shouldThrowExceptionIfTheIssuerIdNotExists() throws ApiNotAccessibleException, IOException, InvalidIssuerIdException {
        IssuerDTO issuer = issuersService.getIssuerConfig("Issuer3id");
    }

    @Test(expected = ApiNotAccessibleException.class)
    public void shouldThrowApiNotAccessibleExceptionWhenIssuersJsonStringIsNullForGettingIssuerConfig() throws IOException, ApiNotAccessibleException, InvalidIssuerIdException {
        Mockito.when(utilities.getIssuersConfigJsonValue()).thenReturn(null);

        issuersService.getIssuerConfig("Issuers1id");
    }
    @Test
    public void shouldReturnOnlyEnabledIssuers() throws IOException, ApiNotAccessibleException {
        IssuersDTO issuers = new IssuersDTO();
        IssuerDTO enabledIssuer = getIssuerConfigDTO("Issuer1", Collections.emptyList());
        IssuerDTO disbaledIssuer = getIssuerConfigDTO("Issuer2", Collections.emptyList());
        disbaledIssuer.setEnabled("false");
        issuers.setIssuers(List.of(enabledIssuer, disbaledIssuer));
        Mockito.when(utilities.getIssuersConfigJsonValue()).thenReturn(new Gson().toJson(issuers));

        IssuersDTO expectedIssuersDTO = new IssuersDTO();
        expectedIssuersDTO.setIssuers(List.of(enabledIssuer));

        IssuersDTO actualIssuersDTO = issuersService.getAllIssuers("");
        assertEquals(expectedIssuersDTO, actualIssuersDTO);
        assertEquals(actualIssuersDTO.getIssuers().get(0).getEnabled(), "true");
        assertEquals(actualIssuersDTO.getIssuers().size(), 1);
    }
}
