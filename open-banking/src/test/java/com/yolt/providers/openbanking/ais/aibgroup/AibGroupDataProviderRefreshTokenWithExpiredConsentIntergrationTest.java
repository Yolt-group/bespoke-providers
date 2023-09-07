package com.yolt.providers.openbanking.ais.aibgroup;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolt.providers.common.ais.url.UrlRefreshAccessMeansRequest;
import com.yolt.providers.common.ais.url.UrlRefreshAccessMeansRequestBuilder;
import com.yolt.providers.common.cryptography.RestTemplateManager;
import com.yolt.providers.common.cryptography.Signer;
import com.yolt.providers.common.domain.authenticationmeans.BasicAuthenticationMean;
import com.yolt.providers.common.exception.TokenInvalidException;
import com.yolt.providers.openbanking.ais.configuration.OpenbankingConfiguration;
import com.yolt.providers.openbanking.ais.generic2.GenericBaseDataProvider;
import com.yolt.providers.openbanking.ais.generic2.SignerMock;
import com.yolt.providers.openbanking.ais.generic2.configuration.resttemplatemanager.RestTemplateManagerMock;
import com.yolt.providers.openbanking.ais.generic2.domain.AccessMeans;
import nl.ing.lovebird.providershared.AccessMeansDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = {AibGroupApp.class, OpenbankingConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("aib")
@AutoConfigureWireMock(stubs = "classpath:/stubs/aibgroup/v31/client_secret/refresh-token-expired/", httpsPort = 0, port = 0)
public class AibGroupDataProviderRefreshTokenWithExpiredConsentIntergrationTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TEST_REDIRECT_URL = "https://www.test-url.com/";
    private static final Signer SIGNER = new SignerMock();

    private Map<String, BasicAuthenticationMean> authenticationMeans;
    private RestTemplateManager restTemplateManager = new RestTemplateManagerMock(() -> UUID.randomUUID().toString());

    @Autowired
    @Qualifier("AibDataProviderV6")
    private GenericBaseDataProvider aibDataProviderV6;

    @Autowired
    @Qualifier("AibNIDataProviderV6")
    private GenericBaseDataProvider aibNIDataProviderV6;

    @Autowired
    @Qualifier("AibIeDataProviderV1")
    private GenericBaseDataProvider aibIeDataProviderV1;

    @Autowired
    @Qualifier("OpenBanking")
    private ObjectMapper objectMapper;

    private Stream<GenericBaseDataProvider> getAibProviders() {
        return Stream.of(aibDataProviderV6, aibNIDataProviderV6, aibIeDataProviderV1);
    }


    @BeforeEach
    void beforeEach() throws IOException, URISyntaxException {
        authenticationMeans = AibGroupSampleAuthenticationMeans.getAibGroupSampleAuthenticationMeansForAis();
    }

    @ParameterizedTest
    @MethodSource("getAibProviders")
    void shouldThrowInvalidTokenExceptionWhenRefreshingTokenWithExpiredConsent(GenericBaseDataProvider aibDataProvider) throws Exception {
        // given
        AccessMeans tokenForExpiredConsent = new AccessMeans(
                Instant.now(),
                USER_ID,
                "accessToken",
                "expiredConsentToken",
                Date.from(Instant.now().plus(1, ChronoUnit.DAYS)),
                Date.from(Instant.now()),
                TEST_REDIRECT_URL);

        AccessMeansDTO accessMeans = new AccessMeansDTO(USER_ID, objectMapper.writeValueAsString(tokenForExpiredConsent), new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        UrlRefreshAccessMeansRequest urlRefreshAccessMeans = new UrlRefreshAccessMeansRequestBuilder()
                .setRestTemplateManager(restTemplateManager)
                .setAccessMeans(accessMeans)
                .setAuthenticationMeans(authenticationMeans)
                .setSigner(SIGNER)
                .build();

        // when
        assertThatThrownBy(() -> aibDataProvider.refreshAccessMeans(urlRefreshAccessMeans))
                .isExactlyInstanceOf(TokenInvalidException.class);
    }
}
