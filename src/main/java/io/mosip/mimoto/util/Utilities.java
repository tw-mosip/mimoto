package io.mosip.mimoto.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.mimoto.core.http.ResponseWrapper;
import io.mosip.mimoto.exception.ExceptionUtils;
import io.mosip.mimoto.exception.PlatformErrorMessages;
import io.mosip.mimoto.service.impl.CredentialShareServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.UrlValidator;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static io.mosip.mimoto.constant.LoggerFileConstant.DELIMITER;
import static io.mosip.mimoto.controller.IdpController.getErrorResponse;

@Component
@Data
public class Utilities {
    private ClassLoader classLoader = Utilities.class.getClassLoader();

    public ObjectMapper objectMapper = new ObjectMapper();

    @Value("${credential.template}")
    private String defaultTemplate;

    @Value("${credential.data.path}")
    private String dataPath;

    // Sample decrypted VC
    private String SAMPLE_VCD = "%s_VCD.json";

    // Sample VC websub event
    private String SAMPLE_EVENT = "%s_EVENT.json";

    private Logger logger = LoggerUtil.getLogger(Utilities.class);


    @Autowired
    private RestApiClient restApiClient;

    /**
     * The config server file storage URL.
     */
    @Value("${config.server.file.storage.uri}")
    private String configServerFileStorageURL;

    /**
     * The get reg issuers config json.
     */
    @Value("${mosip.openid.issuers}")
    private String issuersConfigPath;

    @Value("${mosip.openid.verifiers}")
    private String trustedVerifiersPath;

    @Value("${mosip.openid.htmlTemplate}")
    private String credentialTemplatePath;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private String issuersConfigJsonString = null;

    private String trustedVerifiersJsonString = null;

    private String credentialTemplateHtmlString = null;

    UrlValidator validator;

    @PostConstruct
    public void init(){
        validator = new UrlValidator();
    }

    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            imageString = encoder.encodeToString(imageBytes);
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public JSONObject getTemplate() throws IOException {
        return objectMapper.readValue(classLoader.getResourceAsStream(defaultTemplate), JSONObject.class);
    }

    public JsonNode getVC(String requestId) throws IOException {
        Path resourcePath = Path.of(dataPath, String.format(CredentialShareServiceImpl.VC_JSON_FILE_NAME, requestId));
        if (Files.exists(resourcePath)) {
            return objectMapper.readValue(resourcePath.toFile(), JsonNode.class);
        }
        return null;
    }

    public JsonNode getRequestVC(String requestId) throws IOException {
        Path resourcePath = Path.of(dataPath, String.format(CredentialShareServiceImpl.VC_REQUEST_FILE_NAME, requestId));
        if (Files.exists(resourcePath)) {
            return objectMapper.readValue(resourcePath.toFile(), JsonNode.class);
        }
        return null;
    }

    public JsonNode getDecryptedVC(String requestId) throws IOException {
        Path resourcePath = Path.of(dataPath, String.format(CredentialShareServiceImpl.CARD_JSON_FILE_NAME, requestId));
        if (Files.exists(resourcePath)) {
            return objectMapper.readValue(resourcePath.toFile(), JsonNode.class);
        }
        return null;
    }

    public void removeCacheData(String requestId) throws IOException {
        List<Path> filePathList = new ArrayList<Path>();
        filePathList.add(Path.of(dataPath, String.format(CredentialShareServiceImpl.VC_REQUEST_FILE_NAME, requestId)));
        filePathList.add(Path.of(dataPath, String.format(CredentialShareServiceImpl.EVENT_JSON_FILE_NAME, requestId)));
        filePathList.add(Path.of(dataPath, String.format(CredentialShareServiceImpl.VC_JSON_FILE_NAME, requestId)));
        filePathList.add(Path.of(dataPath, String.format(CredentialShareServiceImpl.CARD_JSON_FILE_NAME, requestId)));
        for (Path filePath : filePathList) {
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                } catch (Exception e) {
                    logger.error("Cannot delete file: " + filePath, e);
                }
            }
        }
    }

    /**
     * Gets the json.
     *
     * @param configServerFileStorageURL the config server file storage URL
     * @param uri                        the uri
     * @return the json
     */
    public String getJson(String configServerFileStorageURL, String uri ) {
        String json = null;
        try {
            json = restApiClient.getApi(URI.create(configServerFileStorageURL + uri), String.class);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return json;
    }

    public String getIssuersConfigJsonValue() throws IOException {
        issuersConfigJsonString = getConfigJsonString(issuersConfigJsonString, configServerFileStorageURL, issuersConfigPath);
        return issuersConfigJsonString;
    }
    public String getTrustedVerifiersJsonValue() throws IOException {
        trustedVerifiersJsonString = getConfigJsonString(trustedVerifiersJsonString, configServerFileStorageURL, trustedVerifiersPath);
        return trustedVerifiersJsonString;
    }
    public String getCredentialSupportedTemplateString() throws IOException {
        credentialTemplateHtmlString = getConfigJsonString(credentialTemplateHtmlString, configServerFileStorageURL, credentialTemplatePath);
        return credentialTemplateHtmlString;
    }

    private String getConfigJsonString(String configJsonString, String configServerFileStorageURL, String resourcePath) throws IOException {
        logger.info("\n configJsonString -> " + configJsonString);
        logger.info("\n configServerFileStorageURL -> " + configServerFileStorageURL);
        logger.info("\n resourcePath -> " + resourcePath);
        if(!StringUtils.isEmpty(configJsonString)){
            return configJsonString;
        }
        if(!validator.isValid(configServerFileStorageURL + resourcePath)){
            Resource resource = new ClassPathResource(resourcePath);
            return Files.readString(resource.getFile().toPath());
        }
        return getJson(configServerFileStorageURL, resourcePath);
    }
    public static ResponseWrapper<Object> handleExceptionWithErrorCode(Exception exception) {
        String errorMessage = exception.getMessage();
        String errorCode = PlatformErrorMessages.MIMOTO_IDP_GENERIC_EXCEPTION.getCode();

        if(errorMessage.contains(DELIMITER)){
            String[] errorSections = errorMessage.split(DELIMITER);
            errorCode = errorSections[0];
            errorMessage = errorSections[1];
        }
        return getErrorResponse(errorCode, errorMessage);
    }
}
