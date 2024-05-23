package io.mosip.mimoto.util;

import com.google.gson.Gson;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.mimoto.core.http.RequestWrapper;
import io.mosip.mimoto.dto.SecretKeyRequest;
import io.mosip.mimoto.exception.TokenGenerationFailedException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Objects;

/**
 * The Class RestApiClient.
 *
 * @author Rishabh Keshari
 */
@Component
public class RestApiClient {

    public static final String TOKEN = "token";
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * The logger.
     */
    private Logger logger = LoggerUtil.getLogger(RestApiClient.class);
    private static final String AUTHORIZATION = "Authorization=";
    /**
     * The builder.
     */
    @Autowired
    @Qualifier("selfTokenRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("plainRestTemplate")
    private RestTemplate plainRestTemplate;

    @Value("${wallet.binding.partner.id}")
    private String partnerId;

    @Value("${wallet.binding.partner.api.key}")
    private String partnerApiKey;

    @Value("${mosip.authmanager.client-token-endpoint}")
    private String authBaseUrl;

    @Value("${mosip.iam.adapter.clientid}")
    private String clientId;

    @Value("${mosip.iam.adapter.clientsecret}")
    private String secret;

    @Value("${mosip.iam.adapter.appid}")
    private String appId;

    @Value("${mosip.iam.adapter.disable-self-token-rest-template:true}")
    private boolean disableSelfTokenRestTemplate;

    @Autowired
    Environment environment;

    /**
     * HTTP GET API
     *
     * @param <T>
     * @param uri
     * @param responseType
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T> T getApi(URI uri, Class<?> responseType) throws Exception {
        T result = null;
        RestTemplate rt = restTemplate;
        if (disableSelfTokenRestTemplate) {
            rt = plainRestTemplate;
        }
        try {
            logger.info("RestApiClient::getApi()::entry uri: {}", uri);
            result = (T) rt.exchange(uri, HttpMethod.GET, setRequestHeader(null, null), responseType)
                    .getBody();
        } catch (Exception e) {
            logger.error("RestApiClient::getApi()::error uri: {} {} {}", uri, e.getMessage(), e);
        }
        return result;
    }

    /**
     * HTTP GET API
     *
     * @param <T>
     * @param url
     * @param responseType
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getApi(String url, Class<?> responseType) {
        T result = null;
        RestTemplate rt = restTemplate;
        if (disableSelfTokenRestTemplate) {
            rt = plainRestTemplate;
        }
        try {
            result = (T) rt.getForObject(url, responseType);
        } catch (Exception e) {
            logger.error("RestApiClient::getApi()::error uri:{} {} {}", url, e.getMessage(), e);
        }
        return result;
    }

    /**
     * HTTP POST API
     *
     * @param <T>
     * @param uri
     * @param mediaType
     * @param requestType
     * @param responseClass
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T> T postApi(String uri, MediaType mediaType, Object requestType, Class<?> responseClass) throws Exception {
        T result = null;
        RestTemplate rt = restTemplate;
        if (disableSelfTokenRestTemplate) {
            rt = plainRestTemplate;
        }
        try {
            logger.info("RestApiClient::postApi()::entry uri: {}", uri);
            result = (T) rt.postForObject(uri, setRequestHeader(requestType, mediaType), responseClass);
        } catch (Exception e) {
            logger.error("RestApiClient::postApi()::error uri: {} {} {}", uri, e.getMessage(), e);
        }
        return result;
    }

    public <T> T postApi(String uri, MediaType mediaType, Object requestType, Class<?> responseClass, boolean useBearerToken) throws Exception {
        T result = null;
        try {
            logger.info("RestApiClient::postApi()::entry uri: {}", uri);
            result = (T) plainRestTemplate.postForObject(uri, setRequestHeader(requestType, mediaType, useBearerToken), responseClass);
        } catch (Exception e) {
            logger.error("RestApiClient::postApi()::error uri: {} {} {}", uri, e.getMessage(), e);
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException ex = (HttpClientErrorException)e;
                if (ex.getStatusCode().value() == 401) {
                    // bearer token renew logic. Set token as empty so that it will auto-renew
                    System.setProperty(TOKEN, "");
                    // try one more time to pass existing call
                    result = (T) plainRestTemplate.postForObject(
                            uri, setRequestHeader(requestType, mediaType, useBearerToken), responseClass);
                }
            }
        }
        return result;
    }

    public <T> T postApi(String uri, MediaType mediaType, Object requestType, Class<?> responseClass, String bearerToken){
        T result = null;
        try {
            logger.info("RestApiClient::postApi()::entry uri: {}", uri);
            result = (T) plainRestTemplate.postForObject(uri, setRequestHeader(requestType, mediaType, bearerToken), responseClass);
        } catch (Exception e) {
            logger.error("RestApiClient::postApi()::error uri: {} {} {}", uri, e.getMessage(), e);
        }
        return result;
    }

    private HttpEntity<Object> setRequestHeader(Object requestType, MediaType mediaType, String bearerToken){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        if (mediaType != null) {
            headers.add(CONTENT_TYPE, mediaType.toString());
        }
        headers.add("Authorization", "Bearer "+bearerToken);
        if (requestType != null) {
            try {
                HttpEntity<Object> httpEntity = (HttpEntity<Object>) requestType;
                HttpHeaders httpHeader = httpEntity.getHeaders();
                Iterator<String> iterator = httpHeader.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (!(headers.containsKey(CONTENT_TYPE) && key.equals(CONTENT_TYPE)))
                        headers.add(key, Objects.requireNonNull(httpHeader.get(key)).get(0));
                }

                return new HttpEntity<Object>(httpEntity.getBody(), headers);
            } catch (ClassCastException | NullPointerException e) {
                return new HttpEntity<Object>(requestType, headers);
            }
        } else
            return new HttpEntity<Object>(headers);
    }

    private HttpEntity<Object> setRequestHeader(Object requestType, MediaType mediaType) throws IOException {
        return setRequestHeader(requestType, mediaType, false);
    }

    /**
     * this method sets token to header of the request
     *
     * @param requestType
     * @param mediaType
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private HttpEntity<Object> setRequestHeader(Object requestType, MediaType mediaType, boolean useBearerToken) throws IOException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        if (mediaType != null) {
            headers.add(CONTENT_TYPE, mediaType.toString());
        }

        if (useBearerToken) {
            String bearerToken = System.getProperty(TOKEN);
            if (StringUtils.isEmpty(bearerToken))
                bearerToken = getBearerToken();
            headers.add("Authorization", bearerToken);
            headers.add("partner-id", partnerId);
            headers.add("partner-api-key", partnerApiKey);
        }

        if (requestType != null) {
            try {
                HttpEntity<Object> httpEntity = (HttpEntity<Object>) requestType;
                HttpHeaders httpHeader = httpEntity.getHeaders();
                Iterator<String> iterator = httpHeader.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (!(headers.containsKey(CONTENT_TYPE) && key.equals(CONTENT_TYPE)))
                        headers.add(key, Objects.requireNonNull(httpHeader.get(key)).get(0));
                }
                return new HttpEntity<Object>(httpEntity.getBody(), headers);
            } catch (ClassCastException | NullPointerException e) {
                return new HttpEntity<Object>(requestType, headers);
            }
        } else
            return new HttpEntity<Object>(headers);
    }

    private String getBearerToken() throws IOException {

        SecretKeyRequest request = new SecretKeyRequest(clientId, secret, appId);
        RequestWrapper<SecretKeyRequest> req = new RequestWrapper<>();
        req.setRequest(request);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(authBaseUrl);
        Gson gson = new Gson();

        StringEntity postingString = new StringEntity(gson.toJson(req));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);
        Header[] cookie = response.getHeaders("Set-Cookie");
        if (cookie.length == 0)
            throw new TokenGenerationFailedException();
        String token = response.getHeaders("Set-Cookie")[0].getValue();
        token = token.replace(AUTHORIZATION, "");
        token = "Bearer " + token.substring(0, token.indexOf(';'));
        System.setProperty(TOKEN, token);
        return token;
    }
}
