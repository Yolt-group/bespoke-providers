package com.yolt.providers.monorepogroup;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.yolt.providers.common.cryptography.RestTemplateManager;
import com.yolt.providers.common.rest.ExternalRestTemplateBuilderFactory;
import com.yolt.providers.common.rest.YoltProxySelectorBuilder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
public class TestConfiguration {

    @Bean
    MeterRegistry mockedMeterRegistry() {
        return new SimpleMeterRegistry(SimpleConfig.DEFAULT,  new MockClock());
    }

    @Bean
    WireMockConfigurationCustomizer optionsCustomizer() {
        return options -> options
                .extensions(new ResponseTemplateTransformer(true))
                .keystoreType("pkcs12")
                .keystorePath("src/test/resources/certificates/fake-keystore.p12")
                .keystorePassword("changeit")
                .keyManagerPassword("changeit");
    }

    @Bean
    YoltProxySelectorBuilder getYoltProxySelectorBuilder() {
        return new YoltProxySelectorBuilder("Dummy", 0);
    }

    @Bean
    RestTemplateManager getRestTemplateManager() {
        ExternalRestTemplateBuilderFactory externalRestTemplateBuilderFactory = new ExternalRestTemplateBuilderFactory();
        externalRestTemplateBuilderFactory.requestFactory(SimpleClientHttpRequestFactory::new);
        return new RestTemplateManagerMock(externalRestTemplateBuilderFactory);
    }

    @Bean
    Clock clock() {
        return Clock.fixed(Instant.parse("2022-01-01T00:00:00Z"), ZoneId.of("UTC"));
    }
}
