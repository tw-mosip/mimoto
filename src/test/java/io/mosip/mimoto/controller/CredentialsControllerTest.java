package io.mosip.mimoto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.mimoto.dto.mimoto.VCCredentialResponse;
import io.mosip.mimoto.service.impl.CredentialServiceImpl;
import io.mosip.vercred.exception.ProofTypeNotFoundException;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.File;

import static io.mosip.mimoto.exception.PlatformErrorMessages.PROOF_TYPE_NOT_SUPPORTED_EXCEPTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CredentialsController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableWebMvc
public class CredentialsControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CredentialServiceImpl credentialService;


    @Test
    public void verifyCredentialTest() throws Exception {
        File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "responses/VerifiableCredential.json");

        ObjectMapper objectMapper = new ObjectMapper();
        VCCredentialResponse credential = objectMapper.readValue(file, VCCredentialResponse.class);

        Mockito.when(credentialService.verifyCredential(credential))
                .thenReturn(true)
                .thenThrow(new ProofTypeNotFoundException("Proof Type available in received credentials is not matching with supported proof terms"));

        this.mockMvc.perform(post("/credentials/verify")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtils.javaObjectToJsonString(credential))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response", Matchers.is(true)));


        this.mockMvc.perform(post("/credentials/verify")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtils.javaObjectToJsonString(credential))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorCode", Matchers.is(PROOF_TYPE_NOT_SUPPORTED_EXCEPTION.getCode())))
                .andExpect(jsonPath("$.errors[0].errorMessage", Matchers.is(PROOF_TYPE_NOT_SUPPORTED_EXCEPTION.getMessage())));


    }
}