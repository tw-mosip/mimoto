package io.mosip.mimoto.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.mimoto.bridge.VCIClientBridge;
import io.mosip.mimoto.model.CredentialMetadata;
import io.mosip.mimoto.model.VerifiableCredential;
import io.mosip.mimoto.dto.IssuerDTO;
import io.mosip.mimoto.dto.idp.TokenResponseDTO;
import io.mosip.mimoto.dto.mimoto.CredentialsSupportedResponse;
import io.mosip.mimoto.dto.mimoto.IssuerConfig;
import io.mosip.mimoto.dto.mimoto.VCCredentialResponse;
import io.mosip.mimoto.dto.mimoto.VerifiableCredentialResponseDTO;
import io.mosip.mimoto.dto.resident.WalletCredentialResponseDTO;
import io.mosip.mimoto.exception.*;
import io.mosip.mimoto.repository.WalletCredentialsRepository;
import io.mosip.mimoto.service.CredentialPDFGeneratorService;
import io.mosip.mimoto.service.IssuersService;
import io.mosip.mimoto.service.WalletCredentialService;
import io.mosip.mimoto.util.CredentialProcessor;
import io.mosip.mimoto.util.EncryptionDecryptionUtil;
import io.mosip.mimoto.util.RestApiClient;
import io.mosip.vciclient.clientMetadata.ClientMetadata;
import io.mosip.vciclient.constants.CredentialFormat;
import io.mosip.vciclient.credentialResponse.CredentialResponse;
import io.mosip.vciclient.issuerMetadata.IssuerMetadata;
import kotlin.jvm.functions.Function4;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static io.mosip.mimoto.exception.ErrorConstants.*;

/**
 * Implementation of {@link WalletCredentialService} for managing wallet credentials.
 */
@Slf4j
@Service
public class WalletCredentialServiceImpl implements WalletCredentialService {

    @Value("${mosip.inji.wallet.issuersWithSingleVcLimit:Mosip}")
    private String issuersWithSingleVcLimit;

    private final WalletCredentialsRepository repository;
    private final IssuersService issuersService;
    private final CredentialProcessor credentialProcessor;
    private final ObjectMapper objectMapper;
    private final EncryptionDecryptionUtil encryptionDecryptionUtil;
    private final CredentialPDFGeneratorService credentialPDFGeneratorService;
    private final RestApiClient restApiClient;

    @Autowired
    public WalletCredentialServiceImpl(WalletCredentialsRepository repository,
                                       IssuersService issuersService,
                                       CredentialProcessor credentialProcessor,
                                       ObjectMapper objectMapper,
                                       EncryptionDecryptionUtil encryptionDecryptionUtil, CredentialPDFGeneratorService credentialPDFGeneratorService, RestApiClient restApiClient) {
        this.repository = repository;
        this.issuersService = issuersService;
        this.credentialProcessor = credentialProcessor;
        this.objectMapper = objectMapper;
        this.encryptionDecryptionUtil = encryptionDecryptionUtil;
        this.credentialPDFGeneratorService = credentialPDFGeneratorService;
        this.restApiClient = restApiClient;
    }

    @Override
    public VerifiableCredentialResponseDTO downloadVCAndStoreInDB(String issuerId, String credentialConfigurationId,
                                                                  TokenResponseDTO tokenResponse,
                                                                  String locale, String walletId, String base64Key)
            throws CredentialProcessingException, ExternalServiceUnavailableException {
        log.info("Fetching and storing credential for wallet: {}, issuer: {}, type: {}", walletId, issuerId, credentialConfigurationId);

        Set<String> issuers = Arrays.stream(issuersWithSingleVcLimit.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        if (issuers.contains(issuerId) && repository.existsByIssuerIdAndCredentialTypeAndWalletId(issuerId, credentialConfigurationId, walletId)) {
            log.warn("Duplicate credential found for issuer: {}, type: {}, wallet: {}", issuerId, credentialConfigurationId, walletId);
            throw new InvalidRequestException(CREDENTIAL_DOWNLOAD_EXCEPTION.getErrorCode(), "Duplicate credential for issuer and type");
        }


        VerifiableCredentialResponseDTO credential;

        credential = credentialProcessor.downloadCredentialAndStoreInDB(
                tokenResponse, credentialConfigurationId, walletId, base64Key, issuerId, locale);

        log.debug("Credential stored successfully: {}", credential.getCredentialId());
        return credential;
    }

    @Override
    public String getProofJWT(String issuerId, String credentialConfigurationId,
                              String accessToken,
                              String walletId, String base64Key)
            throws Exception {
        return credentialProcessor.getProofJwt(accessToken, credentialConfigurationId, walletId, base64Key, issuerId);
    }

    @Override
    public List<VerifiableCredentialResponseDTO> fetchAllCredentialsForWallet(String walletId, String base64Key, String locale) {
        log.info("Fetching all credentials for wallet: {}", walletId);

        List<VerifiableCredential> credentials = repository.findByWalletIdOrderByCreatedAtDesc(walletId);

        return credentials.stream().map(credential -> {
            String issuerId = credential.getCredentialMetadata().getIssuerId();
            IssuerConfig issuerConfig = null;
            try {
                issuerConfig = issuersService.getIssuerConfig(issuerId, credential.getCredentialMetadata().getCredentialType());
            } catch (ApiNotAccessibleException e) {
                log.error("Failed to fetch issuer details for issuerId: {}", issuerId, e);
            }
            return VerifiableCredentialResponseDTO.fromIssuerConfig(issuerConfig, locale, credential.getId());
        }).toList();

    }

    @Override
    public VerifiableCredentialResponseDTO downloadCredentialData(String locale, String walletId, String issuerId, String credentialConfigurationId, String base64EncodedWalletKey, String traceId) throws Exception {
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

        kotlin.jvm.functions.Function1<String, String> getAuthCode = (authorizationEndpoint) -> {
            log.info("Authorization Endpoint received: {}", authorizationEndpoint);
            // make network call to authorization endpoint to get auth code
            try {
                restApiClient.postApi(authorizationEndpoint, null, null, String.class);
                //After success auth , redirected to redirection page with code in search params
                // get the info from the url of redirection page
            } catch (Exception e) {
                log.error("Failed to fetch credential data for issuerId: {}", issuerId, e);
                throw new RuntimeException(e);
            }
            return "code"; // Replace with actual auth code retrieval logic
        };

        Function4<String, String, Map<String, ?>, String, String> getProofJwtCallback = (accessToken, cNonce, issuerMetadata1, credentialConfigurationId1) -> {
            try {
                String proofJWT = this.getProofJWT(issuerId, credentialConfigurationId, accessToken, walletId, base64EncodedWalletKey);
                log.debug("Generated proof JWT successfully: {}", proofJWT);
                return proofJWT;
            } catch (Exception e) {
                log.error("Error generating proof JWT for issuer: {}, credentialConfigurationId: {}, walletId: {}", issuerId, credentialConfigurationId, walletId, e);
                throw new RuntimeException(e);
            }
        };
        CredentialResponse credentialResponse = VCIClientBridge.Companion.requestCredentialFromTrustedIssuerBridgeCaller(traceId, issuerMetadata, clientMetadata, getProofJwtCallback, getAuthCode, 10000);

        return this.saveCredential(credentialResponse, base64EncodedWalletKey, issuerId, credentialConfigurationId, walletId, locale);
    }

    @Override
    public VerifiableCredentialResponseDTO saveCredential(CredentialResponse credentialResponse, String base64Key,
                                                          String issuerId, String credentialConfigurationId, String walletId, String locale) throws Exception {
        return credentialProcessor.storeCredential(credentialResponse, issuerId, credentialConfigurationId, walletId, locale, base64Key);
    }

    @Override
    public WalletCredentialResponseDTO fetchVerifiableCredential(String walletId, String credentialId,
                                                                 String base64Key, String locale)
            throws CredentialNotFoundException, CredentialProcessingException {
        log.info("Fetching credential: {} for wallet: {}", credentialId, walletId);
        VerifiableCredential credential = repository.findByIdAndWalletId(credentialId, walletId)
                .orElseThrow(getCredentialNotFoundExceptionSupplier(walletId, credentialId));

        try {
            String decryptedCredential = encryptionDecryptionUtil.decryptCredential(credential.getCredential(), base64Key);

            WalletCredentialResponseDTO response = generateCredentialResponse(decryptedCredential, credential.getCredentialMetadata(), locale);
            log.debug("Credential fetched successfully: {}", credentialId);
            return response;
        } catch (DecryptionException e) {
            log.error("Decryption failed for credential: {}", credentialId, e);
            throw new CredentialProcessingException(CREDENTIAL_FETCH_EXCEPTION.getErrorCode(), "Decryption failed", e);
        }
    }

    @Override
    public void deleteCredential(String credentialId, String walletId) throws CredentialNotFoundException {
        log.info("Deleting credential with ID: {} for wallet: {}", credentialId, walletId);

        repository.findByIdAndWalletId(credentialId, walletId)
                .orElseThrow(getCredentialNotFoundExceptionSupplier(walletId, credentialId));
        // Delete the credential
        repository.deleteById(credentialId);
        log.info("Successfully deleted credential with ID: {}", credentialId);
    }

    @NotNull
    private static Supplier<CredentialNotFoundException> getCredentialNotFoundExceptionSupplier(String walletId, String credentialId) {
        return () -> {
            log.warn("Credential not found: {} for wallet: {}", credentialId, walletId);
            return new CredentialNotFoundException(RESOURCE_NOT_FOUND.getErrorCode(), RESOURCE_NOT_FOUND.getErrorMessage());
        };
    }

    private WalletCredentialResponseDTO generateCredentialResponse(String decryptedCredential, CredentialMetadata credentialMetadata, String locale) throws CredentialProcessingException {
        log.info("Generating credential response for issuerId: {}, credentialType: {}", credentialMetadata.getIssuerId(), credentialMetadata.getCredentialType());
        try {
            // Parse decrypted credential
            VCCredentialResponse vcCredentialResponse = objectMapper.readValue(decryptedCredential, VCCredentialResponse.class);

            // Fetch issuer details
            IssuerDTO issuerDTO = issuersService.getIssuerDetails(credentialMetadata.getIssuerId());

            // Fetch issuer configuration
            IssuerConfig issuerConfig = issuersService.getIssuerConfig(credentialMetadata.getIssuerId(), credentialMetadata.getCredentialType());

            if (null == issuerConfig) {
                log.error("Credentials supported response not found in wellknown for credentialType: {}", credentialMetadata.getCredentialType());
                throw new CredentialProcessingException(CREDENTIAL_FETCH_EXCEPTION.getErrorCode(), "Invalid credential type configuration");
            }

            // Find credentials supported response for the credential type
            CredentialsSupportedResponse credentialsSupportedResponse = issuerConfig.getCredentialsSupportedResponse();
            if (credentialsSupportedResponse == null || !credentialsSupportedResponse.getCredentialDefinition().getType().containsAll(vcCredentialResponse.getCredential().getType())) {
                log.error("Credentials supported response not found for credentialType: {}", credentialMetadata.getCredentialType());
                throw new CredentialProcessingException(CREDENTIAL_FETCH_EXCEPTION.getErrorCode(), "Invalid credential type configuration");
            }

            // Generate PDF
            // keep the datashare url and credential validity as defaults in downloading VC as PDF as logged-in user
            // This is because generatePdfForVerifiableCredentials will be used by both logged-in and non-logged-in users
            ByteArrayInputStream pdfStream = credentialPDFGeneratorService.generatePdfForVerifiableCredentials(
                    credentialMetadata.getCredentialType(),
                    vcCredentialResponse,
                    issuerDTO,
                    credentialsSupportedResponse,
                    "",
                    "-1",
                    locale
            );

            // Construct response
            String fileName = String.format("%s_credential.pdf", credentialMetadata.getCredentialType());
            return WalletCredentialResponseDTO.builder()
                    .fileName(fileName)
                    .fileContentStream(new InputStreamResource(pdfStream))
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse decrypted credential for issuerId: {}, credentialType: {}", credentialMetadata.getIssuerId(), credentialMetadata.getCredentialType(), e);
            throw new CredentialProcessingException(CREDENTIAL_FETCH_EXCEPTION.getErrorCode(), "Failed to parse decrypted credential");
        } catch (ApiNotAccessibleException | IOException | AuthorizationServerWellknownResponseException |
                 InvalidWellknownResponseException | InvalidIssuerIdException e) {
            log.error("Failed to fetch issuer details or configuration for issuerId: {}", credentialMetadata.getIssuerId(), e);
            throw new CredentialProcessingException(CREDENTIAL_FETCH_EXCEPTION.getErrorCode(), "Failed to fetch issuer configuration");
        } catch (Exception e) {
            log.error("Failed to generate PDF for credentialType: {}", credentialMetadata.getCredentialType(), e);
            throw new CredentialProcessingException(CREDENTIAL_FETCH_EXCEPTION.getErrorCode(), "Failed to generate credential PDF");
        }
    }
}