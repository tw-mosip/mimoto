package io.mosip.mimoto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.dto.mimoto.AuthorizationServerWellKnownResponse;
import io.mosip.mimoto.service.impl.AuthorizationServerServiceImpl;
import io.mosip.mimoto.util.RestApiClient;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.net.URI;
import java.util.List;
import static io.mosip.mimoto.util.TestUtilities.*;
import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServerServiceTest {

    @InjectMocks
    AuthorizationServerServiceImpl authorizationServerService = new AuthorizationServerServiceImpl();
    
    @Mock
    RestApiClient restApiClient;

    @Mock
    ObjectMapper objectMapper;

    URI authorizationServerWellknownUrl;

    String authorizationServerHostUrl;

    @Before
    public void setUp() {
        authorizationServerWellknownUrl = URI.create("https://dev/authorize/.well-known/oauth-authorization-server");
        authorizationServerHostUrl = "https://dev/authorize";
    }

    @Test
    public void shouldThrowExceptionIfResponseIsNullWhenGettingAuthServerWellknownConfig() {
        String expectedExceptionMessage = "RESIDENT-APP-042 --> Failed to fetch Authorization Server well-known due to:\n" + "well-known api is not accessible";
        String actualExceptionMessage;
        try {
            Mockito.when(restApiClient.getApi(authorizationServerWellknownUrl, String.class)).thenReturn(null);

            authorizationServerService.getWellknown(authorizationServerHostUrl);
        } catch (Exception e) {
            actualExceptionMessage = e.getMessage();
            assertEquals(expectedExceptionMessage, actualExceptionMessage);
        }
    }

    @Test
    public void shouldThrowExceptionIfAuthServerHostUrlIsNullWhenGettingItsWellknownConfig() {
        String expectedExceptionMessage = "RESIDENT-APP-042 --> Failed to fetch Authorization Server well-known due to:\n" + "Authorization Server host url cannot be null";
        String actualExceptionMessage;
        try {
            authorizationServerService.getWellknown(null);
        } catch (Exception e) {
            actualExceptionMessage = e.getMessage();
            assertEquals(expectedExceptionMessage, actualExceptionMessage);
        }
    }

    @Test
    public void shouldThrowExceptionIfAuthServerHostUrlIsInvalidWhenGettingItsWellknownConfig() {
        String expectedExceptionMessage = "RESIDENT-APP-042 --> Failed to fetch Authorization Server well-known due to:\n" + "Illegal character in authority at index 8: https:// dev/authorize/.well-known/oauth-authorization-server";
        String actualExceptionMessage;
        try {
            authorizationServerService.getWellknown("https:// dev/authorize");
        } catch (Exception e) {
            actualExceptionMessage = e.getMessage();
            assertEquals(expectedExceptionMessage, actualExceptionMessage);
        }
    }

    @Test
    public void shouldThrowExceptionIfAuthorizationEndpointIsMissingInAuthServerWellknownResponse() {
        String expectedExceptionMessage = "RESIDENT-APP-042 --> Failed to fetch Authorization Server well-known due to:\n" + "Validation failed:\n" + "authorizationEndpoint: must not be blank";
        String actualExceptionMessage;
        try {
            AuthorizationServerWellKnownResponse expectedAuthorizationServerWellKnownResponse = getAuthServerWellknownResponseDto(List.of("authorization_endpoint"));
            String expectedIssuersConfigJson = getExpectedIssuersConfigJson();
            Mockito.when(restApiClient.getApi(authorizationServerWellknownUrl, String.class)).thenReturn(expectedIssuersConfigJson);
            Mockito.when(objectMapper.readValue(expectedIssuersConfigJson, AuthorizationServerWellKnownResponse.class)).thenReturn(expectedAuthorizationServerWellKnownResponse);

            authorizationServerService.getWellknown(authorizationServerHostUrl);
        } catch (Exception e) {
            actualExceptionMessage = e.getMessage();
            assertEquals(expectedExceptionMessage, actualExceptionMessage);
        }
    }

    @Test
    public void shouldReturnResponseIfTheAuthServerHostUrlAndResponseAreValid() throws Exception {
        AuthorizationServerWellKnownResponse expectedAuthorizationServerWellKnownResponse = getAuthServerWellknownResponseDto(List.of());
        String expectedIssuersConfigJson = getExpectedIssuersConfigJson();
        Mockito.when(restApiClient.getApi(authorizationServerWellknownUrl, String.class)).thenReturn(expectedIssuersConfigJson);
        Mockito.when(objectMapper.readValue(expectedIssuersConfigJson, AuthorizationServerWellKnownResponse.class)).thenReturn(expectedAuthorizationServerWellKnownResponse);

        AuthorizationServerWellKnownResponse actualAuthorizationServerWellKnownResponse = authorizationServerService.getWellknown(authorizationServerHostUrl);

        assertEquals(expectedAuthorizationServerWellKnownResponse, actualAuthorizationServerWellKnownResponse);
    }
}

