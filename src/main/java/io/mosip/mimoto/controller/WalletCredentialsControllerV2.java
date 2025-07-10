package io.mosip.mimoto.controller;

import io.micrometer.tracing.Tracer;
import io.mosip.mimoto.bridge.VCIClientBridge;
import io.mosip.mimoto.constant.SwaggerLiteralConstants;
import io.mosip.mimoto.dto.VerifiableCredentialRequestDTOV2;
import io.mosip.mimoto.dto.mimoto.VerifiableCredentialResponseDTO;
import io.mosip.mimoto.service.AuthSessionRegistry;
import io.mosip.mimoto.service.WalletCredentialService;
import io.mosip.mimoto.util.WalletUtil;
import io.mosip.vciclient.clientMetadata.ClientMetadata;
import io.mosip.vciclient.constants.CredentialFormat;
import io.mosip.vciclient.credentialResponse.CredentialResponse;
import io.mosip.vciclient.issuerMetadata.IssuerMetadata;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kotlin.jvm.functions.Function4;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * REST controller for managing wallet credentials.
 */
@Slf4j
@RestController
@RequestMapping(value = "/wallets/{walletId}/credentials/v2")
@Tag(name = SwaggerLiteralConstants.WALLET_CREDENTIALS_NAME, description = SwaggerLiteralConstants.WALLET_CREDENTIALS_DESCRIPTION)
public class WalletCredentialsControllerV2 {

    private final WalletCredentialService walletCredentialService;
    private final Tracer tracer;
    private final AuthSessionRegistry authSessionRegistry;

    @Autowired
    public WalletCredentialsControllerV2(WalletCredentialService walletCredentialService,
                                         Tracer tracer, AuthSessionRegistry authSessionRegistry) {
        this.walletCredentialService = walletCredentialService;
        this.tracer = tracer;
        this.authSessionRegistry = authSessionRegistry;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> initiateDownload(@RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") @Pattern(regexp = "^[a-z]{2}$", message = "Locale must be a 2-letter code") String locale,
                                                                @PathVariable("walletId") @NotBlank(message = "Wallet ID cannot be blank") String walletId,
                                                                @RequestBody VerifiableCredentialRequestDTOV2 verifiableCredentialRequest,
                                                                HttpSession httpSession) {
        String base64EncodedWalletKey = WalletUtil.getSessionWalletKey(httpSession);

        String issuerId = verifiableCredentialRequest.getIssuer();
        String credentialConfigurationId = verifiableCredentialRequest.getCredentialConfigurationId();

        String traceId = Objects.requireNonNull(tracer.currentSpan()).context().traceId();
        authSessionRegistry.createSession(traceId);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("traceId", traceId);

        IssuerMetadata issuerMetadata = new IssuerMetadata(
                "https://injicertify-mock.released.mosip.net/",
                "https://injicertify-mock.released.mosip.net/v1/certify/issuance/credential",
                List.of("VerifiableCredential", "MockVerifiableCredential"),
                List.of("https://www.w3.org/2018/credentials/v1", "https://api.released.mosip.net/.well-known/mosip-ida-context.json"),
                CredentialFormat.LDP_VC,
                null,
                null,
                List.of("https://esignet-mock.released.mosip.net"),
                "http://localhost:8099/v1/mimoto/get-token/Mock",
                "mock_identity_vc_ldp"
        );

        ClientMetadata clientMetadata = new ClientMetadata("mpartner-default-mimoto-mock-oidc", "http://localhost:3004/redirect");
        kotlin.jvm.functions.Function1<String, String> getAuthCode = authorizationEndpoint -> {
            responseBody.put("authorizationEndpoint", authorizationEndpoint);
            return waitForCode(traceId);
        };

        Function4<String, String, Map<String, ?>, String, String> getProofJwtCallback = (accessToken, cNonce, issuerMetadata1, credentialConfigurationId1) -> {
            try {
                String proofJWT = walletCredentialService.getProofJWT(issuerId, credentialConfigurationId, accessToken, walletId, base64EncodedWalletKey);
                log.debug("Generated proof JWT: {}", proofJWT);
                return proofJWT;
            } catch (Exception e) {
                log.error("Error generating proof JWT for issuer: {}, credentialConfigurationId: {}, walletId: {}", issuerId, credentialConfigurationId, walletId, e);
                throw new RuntimeException(e);
            }
        };

        new Thread(() -> {
            try {
                CredentialResponse credentialResponse = VCIClientBridge.Companion.requestCredentialFromTrustedIssuerBridgeCaller(traceId, issuerMetadata, clientMetadata, getProofJwtCallback, getAuthCode, 10000);
                VerifiableCredentialResponseDTO verifiableCredentialResponseDTO = walletCredentialService.saveCredential(credentialResponse, base64EncodedWalletKey, issuerId, credentialConfigurationId, walletId, locale);
                authSessionRegistry.saveResult(traceId, verifiableCredentialResponseDTO);
            } catch (Exception e) {
                log.error("Error processing credential request for traceId: {}, issuerId: {}, credentialConfigurationId: {}, walletId: {}", traceId, issuerId, credentialConfigurationId, walletId, e);
                e.printStackTrace();
            }
        }).start();


        return ResponseEntity.status(HttpStatus.OK).body(responseBody); // use this traceId to poll for the code later
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam String traceId, @RequestParam String code) {
        authSessionRegistry.fulfill(traceId, code);
        return ResponseEntity.ok("Received code. Flow will resume.");
    }

    @GetMapping("/result")
    public ResponseEntity<Object> getResult(@RequestParam String traceId) {
        Object resource = authSessionRegistry.getResult(traceId);
        if (resource == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Resource not ready");
        }
        return ResponseEntity.ok(resource);
    }

    private String waitForCode(String traceId) {
        try {
            return authSessionRegistry.getFuture(traceId).get(10000, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Timeout waiting for auth code", e);
        }
    }
}