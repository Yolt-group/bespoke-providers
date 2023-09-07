package com.yolt.providers.openbanking.ais.lloydsbankinggroup.ais;

import com.yolt.providers.common.ais.DataProviderResponse;
import com.yolt.providers.common.ais.url.UrlFetchDataRequest;
import com.yolt.providers.common.ais.url.UrlFetchDataRequestBuilder;
import com.yolt.providers.common.cryptography.Signer;
import com.yolt.providers.common.domain.authenticationmeans.BasicAuthenticationMean;
import com.yolt.providers.common.providerinterface.UrlDataProvider;
import com.yolt.providers.openbanking.ais.configuration.OpenbankingConfiguration;
import com.yolt.providers.openbanking.ais.generic2.GenericBaseDataProvider;
import com.yolt.providers.openbanking.ais.generic2.configuration.resttemplatemanager.RestTemplateManagerMock;
import com.yolt.providers.openbanking.ais.generic2.domain.AccessMeans;
import com.yolt.providers.openbanking.ais.lloydsbankinggroup.LloydsGroupApp;
import com.yolt.providers.openbanking.ais.lloydsbankinggroup.LloydsSampleTypedAuthenticationMeans;
import com.yolt.providers.openbanking.ais.utils.OpenBankingTestObjectMapper;
import nl.ing.lovebird.providershared.AccessMeansDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * When there are some server issue on bank side, when calling optional endpoint we want to continue date fetching
 * without breaking the flow
 * <p>
 * Disclaimer: most providers in LBG group are the same from code and stubs perspective (then only difference is configuration)
 * Due to that fact this test class is parametrised, so all providers in group are tested.
 * <p>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = {LloydsGroupApp.class, OpenbankingConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("lloydsgroup")
@AutoConfigureWireMock(stubs = "classpath:/stubs/lloydsbankinggroup/ais/optional-endpoints-500/", httpsPort = 0, port = 0)
public class LloydsGroupDataProviderOptionalEndpoints500IntegrationTest {

    private static final UUID USER_ID = UUID.randomUUID();

    private RestTemplateManagerMock restTemplateManagerMock;

    @Autowired
    @Qualifier("BankOfScotlandDataProviderV10")
    private GenericBaseDataProvider bankOfScotlandDataProviderV10;
    @Autowired
    @Qualifier("BankOfScotlandCorpoDataProviderV8")
    private GenericBaseDataProvider bankOfScotlandCorpoDataProviderV8;
    @Autowired
    @Qualifier("HalifaxDataProviderV10")
    private GenericBaseDataProvider halifaxDataProviderV10;
    @Autowired
    @Qualifier("LloydsBankDataProviderV10")
    private GenericBaseDataProvider lloydsBankDataProviderV10;
    @Autowired
    @Qualifier("LloydsBankCorpoDataProviderV8")
    private GenericBaseDataProvider lloydsBankCorpoDataProviderV8;
    @Autowired
    @Qualifier("MbnaCreditCardDataProviderV6")
    private GenericBaseDataProvider mbnaCreditCardDataProviderV6;

    @Mock
    private Signer signer;

    private Map<String, BasicAuthenticationMean> authenticationMeans;

    private AccessMeans token;

    private String requestTraceId = "c554a9ef-47c1-4b4e-a77f-2ad770d69748";

    @BeforeAll
    public void beforeAll() throws IOException, URISyntaxException {
        authenticationMeans = new LloydsSampleTypedAuthenticationMeans().getAuthenticationMeans();
        restTemplateManagerMock = new RestTemplateManagerMock(() -> requestTraceId);
        token = new AccessMeans();
        token.setCreated(Instant.now());
        token.setAccessToken("accessToken");
        token.setExpireTime(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    }

    @ParameterizedTest
    @MethodSource("getDataProviders")
    public void errorOnOptionalEndpointDoesntBrakeFlowDuringFetchData(UrlDataProvider dataProvider) throws Exception {
        // given
        String serializedAccessMeans = OpenBankingTestObjectMapper.INSTANCE.writeValueAsString(token);

        AccessMeansDTO accessMeans = new AccessMeansDTO(USER_ID, serializedAccessMeans, new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        UrlFetchDataRequest urlFetchData = new UrlFetchDataRequestBuilder()
                .setAccessMeans(accessMeans)
                .setTransactionsFetchStartTime(Instant.now())
                .setAuthenticationMeans(authenticationMeans)
                .setSigner(signer)
                .setRestTemplateManager(restTemplateManagerMock)
                .build();

        // when
        DataProviderResponse dataProviderResponse = dataProvider.fetchData(urlFetchData);

        // then
        assertThat(dataProviderResponse.getAccounts()).hasSize(4);
    }

    private Stream<UrlDataProvider> getDataProviders() {
        return Stream.of(
                bankOfScotlandDataProviderV10, bankOfScotlandCorpoDataProviderV8,
                halifaxDataProviderV10, lloydsBankDataProviderV10,
                lloydsBankCorpoDataProviderV8, mbnaCreditCardDataProviderV6);
    }
}
