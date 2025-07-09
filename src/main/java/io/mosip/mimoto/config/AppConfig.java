package io.mosip.mimoto.config;


import io.mosip.kernel.core.logger.config.SleuthValve;
import io.mosip.pixelpass.PixelPass;
import io.mosip.vercred.vcverifier.CredentialsVerifier;
import org.apache.catalina.Valve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.tracing.Tracer;

@Configuration
public class AppConfig {
    @Bean
    public CredentialsVerifier credentialsVerifier() {
        return new CredentialsVerifier();
    }

    @Bean
    public PixelPass pixelPass() {
        return new PixelPass();
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> sleuthValveCustomizer(Tracer tracer) {
        return factory -> {
            Valve sleuthValve = new SleuthValve(tracer);
            factory.addContextValves(sleuthValve);
        };
    }
}
