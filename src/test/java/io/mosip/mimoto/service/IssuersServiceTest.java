package io.mosip.mimoto.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.IssuersDTO;
import io.mosip.mimoto.dto.mimoto.CredentialIssuerConfigurationResponse;
import io.mosip.mimoto.dto.mimoto.CredentialIssuerWellKnownResponse;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.exception.AuthorizationServerWellknownResponseException;
import io.mosip.mimoto.exception.InvalidIssuerIdException;
import io.mosip.mimoto.exception.InvalidWellknownResponseException;
import io.mosip.mimoto.service.impl.CredentialServiceImpl;
import io.mosip.mimoto.service.impl.IssuersServiceImpl;
import io.mosip.mimoto.util.CredentialIssuerWellknownResponseValidator;
import io.mosip.mimoto.util.RestApiClient;
import io.mosip.mimoto.util.Utilities;
import jakarta.validation.Validator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.mosip.mimoto.util.TestUtilities.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;

@RunWith(MockitoJUnitRunner.class)
public class IssuersServiceTest {

    @InjectMocks
    IssuersServiceImpl issuersService = new IssuersServiceImpl();

    @Mock
    Validator validator;

    @InjectMocks
    CredentialServiceImpl credentialService = new CredentialServiceImpl();

    @Mock
    RestApiClient restApiClient;

    @Mock
    Utilities utilities;

    @Mock
    CredentialIssuerWellknownResponseValidator credentialIssuerWellknownResponseValidator;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    AuthorizationServerService authorizationServerService;

    List<String> issuerConfigRelatedFields11 = List.of("additional_headers", "authorization_endpoint", "authorization_audience", "token_endpoint", "proxy_token_endpoint", "credential_endpoint", "credential_audience", "redirect_uri");
    List<String> issuerConfigRelatedFields = List.of("additional_headers", "authorization_endpoint", "authorization_audience", "credential_endpoint", "credential_audience");

    String wellKnownUrl,issuerId;
    CredentialIssuerConfigurationResponse expectedCredentialIssuerConfigurationResponse;
    IssuersDTO issuers = new IssuersDTO();
    @Before
    public void setUp() throws Exception {

        issuers.setIssuers(List.of(getIssuerConfigDTO("Issuer1", Collections.emptyList()), getIssuerConfigDTO("Issuer3", Collections.emptyList())));
        Mockito.when(utilities.getIssuersConfigJsonValue()).thenReturn(new Gson().toJson(issuers));
        wellKnownUrl = "https://issuer.dev1.mosip.net/.well-known/openid-credential-issuer";
        issuerId = "Issuer1id";
        expectedCredentialIssuerConfigurationResponse = getCredentialIssuerConfigurationResponseDto("Issuer1", Map.of("CredentialType1", getCredentialSupportedResponse("CredentialType1")), List.of());
        CredentialIssuerWellKnownResponse expextedCredentialIssuerWellKnownResponse = getCredentialIssuerWellKnownResponseDto("Issuer1",
                Map.of("CredentialType1", getCredentialSupportedResponse("CredentialType1")), List.of(""));
        Mockito.when(restApiClient.getApi(wellKnownUrl, String.class))
                .thenReturn(getExpectedWellKnownJson());
        Mockito.when(objectMapper.readValue(getExpectedWellKnownJson(), CredentialIssuerWellKnownResponse.class)).thenReturn(expextedCredentialIssuerWellKnownResponse);
        Mockito.when(authorizationServerService.getWellknown("https://dev.net/authorize")).thenReturn(expectedCredentialIssuerConfigurationResponse.getAuthorizationServerWellKnownResponse());
    }

    @Test
    public void shouldReturnIssuersWithIssuerConfigAsNull() throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        IssuersDTO expectedIssuers = new IssuersDTO();
        List<IssuerDTO> issuers = new ArrayList<>(List.of(getIssuerConfigDTO("Issuer1", issuerConfigRelatedFields), getIssuerConfigDTO("Issuer3", issuerConfigRelatedFields)));
        expectedIssuers.setIssuers(issuers);

        IssuersDTO expectedFilteredIssuers = new IssuersDTO();
        List<IssuerDTO> filteredIssuersList = new ArrayList<>(List.of(getIssuerConfigDTO("Issuer1", issuerConfigRelatedFields)));
        expectedFilteredIssuers.setIssuers(filteredIssuersList);

        IssuersDTO allIssuers = issuersService.getAllIssuers(null);
        IssuersDTO filteredIssuers = issuersService.getAllIssuers("Issuer1");
        assertEquals(expectedIssuers, allIssuers);
        assertEquals(expectedFilteredIssuers, filteredIssuers);
    }

    @Test
    public void shouldThrowExceptionIfCredentialIssuerHostIsNullForAnyIssuerWhenFetchingAllIssuersConfig() {
        IssuersDTO expectedIssuers = new IssuersDTO();
        List<IssuerDTO> expectedIssuersList = new ArrayList<>(List.of(getIssuerConfigDTO("Issuer1", issuerConfigRelatedFields), getIssuerConfigDTO("Issuer2", issuerConfigRelatedFields)));
        expectedIssuers.setIssuers(expectedIssuersList);
        issuers.setIssuers(List.of(getIssuerConfigDTO("Issuer1", Collections.emptyList()), getIssuerConfigDTO("Issuer2", Collections.emptyList())));
        Mockito.when(utilities.getIssuersConfigJsonValue()).thenReturn(new Gson().toJson(issuers));

        InvalidWellknownResponseException actualException = assertThrows(InvalidWellknownResponseException.class, () -> {
            issuersService.getAllIssuers(null);
        });

        assertEquals("RESIDENT-APP-041 --> Invalid Wellknown from Issuer\n" +
                "credential_issuer_host cannot be null for issuer Issuer2id", actualException.getMessage());
    }

    @Test(expected = ApiNotAccessibleException.class)
    public void shouldThrowApiNotAccessibleExceptionWhenIssuersJsonStringIsNullForGettingAllIssuers() throws IOException, ApiNotAccessibleException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        Mockito.when(utilities.getIssuersConfigJsonValue()).thenReturn(null);

        issuersService.getAllIssuers(null);
    }

    @Test
    public void shouldReturnIssuerDataAndConfigForTheIssuerIdIfExist() throws ApiNotAccessibleException, IOException, InvalidIssuerIdException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        IssuerDTO expectedIssuer = getIssuerConfigDTO("Issuer1", issuerConfigRelatedFields);

        IssuerDTO issuer = issuersService.getIssuerConfig("Issuer1id");

        assertEquals(expectedIssuer, issuer);
    }

    @Test
    public void shouldReturnIssuerWellknownForTheIssuerIdIfExist() throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        CredentialIssuerWellKnownResponse expextedCredentialIssuerWellKnownResponse = getCredentialIssuerWellKnownResponseDto("Issuer1",
                Map.of("CredentialType1", getCredentialSupportedResponse("CredentialType1")), List.of());
        Mockito.when(restApiClient.getApi(wellKnownUrl, String.class))
                .thenReturn(getExpectedWellKnownJson());
        Mockito.when(objectMapper.readValue(getExpectedWellKnownJson(), CredentialIssuerWellKnownResponse.class)).thenReturn(expextedCredentialIssuerWellKnownResponse);
        lenient().when(validator.validate(expextedCredentialIssuerWellKnownResponse)).thenReturn(Collections.emptySet());

        CredentialIssuerWellKnownResponse credentialIssuerWellKnownResponse = issuersService.getIssuerWellknown(issuerId);

        assertEquals(expextedCredentialIssuerWellKnownResponse, credentialIssuerWellKnownResponse);
        verify(restApiClient, times(2)).getApi(wellKnownUrl, String.class);
    }

    @Test
    public void shouldReturnIssuerDataAndConfigForAllIssuer() throws ApiNotAccessibleException, IOException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        IssuersDTO expectedIssuers = new IssuersDTO();
        List<IssuerDTO> issuers = new ArrayList<>(List.of(getIssuerConfigDTO("Issuer1", new ArrayList<>()), getIssuerConfigDTO("Issuer3", new ArrayList<>())));
        expectedIssuers.setIssuers(issuers);

        IssuersDTO issuersDTO = issuersService.getAllIssuersWithAllFields();

        assertEquals(expectedIssuers, issuersDTO);
    }

    @Test(expected = InvalidIssuerIdException.class)
    public void shouldThrowExceptionIfTheIssuerIdNotExists() throws ApiNotAccessibleException, IOException, InvalidIssuerIdException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        issuersService.getIssuerConfig("Issuer4id");
    }

    @Test(expected = ApiNotAccessibleException.class)
    public void shouldThrowApiNotAccessibleExceptionWhenIssuersJsonStringIsNullForGettingIssuerConfig() throws IOException, ApiNotAccessibleException, InvalidIssuerIdException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
        Mockito.when(utilities.getIssuersConfigJsonValue()).thenReturn(null);

        issuersService.getIssuerConfig("Issuers1id");
    }

    @Test
    public void shouldReturnOnlyEnabledIssuers() throws IOException, ApiNotAccessibleException, AuthorizationServerWellknownResponseException, InvalidWellknownResponseException {
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

    @Test
    public void shouldReturnProperCredentialConfigurationsForTheRequestedIssuer() throws AuthorizationServerWellknownResponseException, ApiNotAccessibleException, IOException, InvalidWellknownResponseException {
        CredentialIssuerConfigurationResponse actualCredentialIssuerConfigurationResponse = issuersService.getIssuerConfiguration("Issuer1id");

        assertEquals(expectedCredentialIssuerConfigurationResponse, actualCredentialIssuerConfigurationResponse);
    }

    @Test
    public void issuersConfigShouldThrowExceptionIfAnyErrorOccurredWhileFetchingIssuersWellknown() throws ApiNotAccessibleException, IOException {
        Mockito.when(utilities.getIssuersConfigJsonValue()).thenReturn(null);

        ApiNotAccessibleException actualException = assertThrows(ApiNotAccessibleException.class, () -> {
            issuersService.getIssuerConfiguration(issuerId);
        });

        assertEquals("RESIDENT-APP-026 --> Api not accessible failure", actualException.getMessage());
        verify(utilities, times(1)).getIssuersConfigJsonValue();
    }


    @Test
    public void issuersConfigShouldThrowExceptionIfAnyErrorOccurredWhileFetchingIssuersAuthorizationServerWellknown() throws JsonProcessingException, AuthorizationServerWellknownResponseException {
        CredentialIssuerWellKnownResponse expextedCredentialIssuerWellKnownResponse = getCredentialIssuerWellKnownResponseDto("Issuer1",
                Map.of("CredentialType1", getCredentialSupportedResponse("CredentialType1")), List.of(""));
        Mockito.when(restApiClient.getApi(wellKnownUrl, String.class))
                .thenReturn(getExpectedWellKnownJson());
        Mockito.when(objectMapper.readValue(getExpectedWellKnownJson(), CredentialIssuerWellKnownResponse.class)).thenReturn(expextedCredentialIssuerWellKnownResponse);
        Mockito.when(authorizationServerService.getWellknown("https://dev.net/authorize")).thenThrow(new AuthorizationServerWellknownResponseException("well-known api is not accessible"));

        AuthorizationServerWellknownResponseException actualException = assertThrows(AuthorizationServerWellknownResponseException.class, () -> {
            issuersService.getIssuerConfiguration("Issuer1id");
        });

        assertEquals("RESIDENT-APP-042 --> Invalid Authorization Server well-known from server:\n" +
                "well-known api is not accessible", actualException.getMessage());
        verify(authorizationServerService, times(1)).getWellknown("https://dev.net/authorize");
    }
}