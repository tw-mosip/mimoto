package io.mosip.mimoto.config;

import io.mosip.kernel.auth.defaultadapter.filter.AuthFilter;
import io.mosip.kernel.auth.defaultadapter.filter.CorsFilter;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {
        "io.mosip.mimoto.*" }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
        Config.class, OpenApiProperties.class, SwaggerConfig.class}))
public class MimotoConfig {

   /* @Override
    protected void configure(HttpSecurity http) throws Exception {
        http = http.csrf().disable();
        http.authorizeRequests().antMatchers("*").authenticated().anyRequest().permitAll().and().exceptionHandling();
        http.headers().cacheControl();
        http.headers().frameOptions().sameOrigin();

    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("*")
                .authenticated()
                .anyRequest()
                .permitAll()

        );
        http.exceptionHandling(exceptionConfigurer  -> new ExceptionHandlingConfigurer());
        http.headers(headersEntry -> {
            headersEntry.cacheControl(Customizer.withDefaults());
            headersEntry.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
        });

        return http.build();
    }

    @Bean
    public RestTemplate selfTokenRestTemplate()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient((HttpClient) httpClient);
        return new RestTemplate(requestFactory);

    }

    @Bean
    public RestTemplate plainRestTemplate()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient((HttpClient) httpClient);
        return new RestTemplate(requestFactory);

    }


    @Bean
    public Map<String, String> injiConfig() {
        return new HashMap<>();
    }
}
