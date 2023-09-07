package com.yolt.providers.openbanking.ais.tidegroup.ais;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolt.providers.common.ais.DataProviderResponse;
import com.yolt.providers.common.ais.url.UrlFetchDataRequest;
import com.yolt.providers.common.ais.url.UrlFetchDataRequestBuilder;
import com.yolt.providers.common.cryptography.Signer;
import com.yolt.providers.common.domain.authenticationmeans.BasicAuthenticationMean;
import com.yolt.providers.openbanking.ais.configuration.OpenbankingConfiguration;
import com.yolt.providers.openbanking.ais.generic2.SignerMock;
import com.yolt.providers.openbanking.ais.generic2.configuration.resttemplatemanager.RestTemplateManagerMock;
import com.yolt.providers.openbanking.ais.generic2.domain.AccessMeans;
import com.yolt.providers.openbanking.ais.tidegroup.TideGroupApp;
import com.yolt.providers.openbanking.ais.tidegroup.TideGroupSampleTypedAuthMeansV2;
import com.yolt.providers.openbanking.ais.tidegroup.common.TideGroupDataProviderV2;
import nl.ing.lovebird.extendeddata.common.CurrencyCode;
import nl.ing.lovebird.extendeddata.transaction.TransactionStatus;
import nl.ing.lovebird.providerdomain.ProviderAccountDTO;
import nl.ing.lovebird.providerdomain.ProviderTransactionType;
import nl.ing.lovebird.providershared.AccessMeansDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test contains missing data flows occurring in Tide group providers. As a business decision provider implementation should return DTO as best effort
 * Covered flows:
 * - fetching accounts, balances, transactions when some data are missing
 * <p>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = {TideGroupApp.class, OpenbankingConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("tidegroup")
@AutoConfigureWireMock(stubs = "classpath:/stubs/tidegroup/ob_3.1.1/ais/v2/missing-data/", port = 0, httpsPort = 0)
public class TideGroupDataProviderV3MissingDataIntegrationTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final Signer SIGNER = new SignerMock();
    private static RestTemplateManagerMock restTemplateManagerMock;
    private static Map<String, BasicAuthenticationMean> authenticationMeans;

    @Autowired
    @Qualifier("TideDataProviderV3")
    private TideGroupDataProviderV2 tideDataProvider;

    @Autowired
    @Qualifier("OpenBanking")
    private ObjectMapper objectMapper;

    private Stream<TideGroupDataProviderV2> getProviders() {
        return Stream.of(tideDataProvider);
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        restTemplateManagerMock = new RestTemplateManagerMock(() -> "12345");
        authenticationMeans = TideGroupSampleTypedAuthMeansV2.getAuthenticationMeans();
    }

    @ParameterizedTest
    @MethodSource("getProviders")
    void shouldFetchDataWhenSomeInformationInBankResponseAreMissing(TideGroupDataProviderV2 subject) throws Exception {
        // given
        AccessMeans token = new AccessMeans(
                Instant.now(),
                null,
                "test-accounts",
                "refreshToken",
                Date.from(Instant.now().plus(1, ChronoUnit.DAYS)),
                null,
                null);
        String serializedAccessMeans = objectMapper.writeValueAsString(token);
        AccessMeansDTO accessMeans = new AccessMeansDTO(USER_ID, serializedAccessMeans, new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        UrlFetchDataRequest urlFetchData = new UrlFetchDataRequestBuilder()
                .setTransactionsFetchStartTime(Instant.now())
                .setAccessMeans(accessMeans)
                .setRestTemplateManager(restTemplateManagerMock)
                .setAuthenticationMeans(authenticationMeans)
                .setSigner(SIGNER)
                .build();

        // when
        DataProviderResponse dataProviderResponse = subject.fetchData(urlFetchData);

        // then
        assertThat(dataProviderResponse.getAccounts()).hasSize(1);
        ProviderAccountDTO account = dataProviderResponse.getAccounts().get(0);
        assertThat(account.getAvailableBalance()).isNull();
        assertThat(account.getCurrentBalance()).isEqualTo("1.00");
        assertThat(account.getCurrency()).isNull();
        assertThat(account.getName()).isEqualTo("TIDE Account");
        assertThat(account.getExtendedAccount().getCurrency()).isNull();
        assertThat(account.getExtendedAccount().getBalances()).hasSize(1);
        assertThat(account.getExtendedAccount().getBalances().get(0).getBalanceAmount().getCurrency()).isEqualTo(CurrencyCode.GBP);
        assertThat(account.getExtendedAccount().getBalances().get(0).getBalanceAmount().getAmount()).isEqualTo("1.00");
        assertThat(account.getTransactions()).hasSize(3);
        assertThat(account.getTransactions().get(0).getAmount()).isNull();
        assertThat(account.getTransactions().get(0).getStatus()).isEqualTo(TransactionStatus.BOOKED);
        assertThat(account.getTransactions().get(0).getType()).isNull();
        assertThat(account.getTransactions().get(0).getExtendedTransaction().getTransactionAmount().getCurrency()).isEqualTo(CurrencyCode.GBP);
        assertThat(account.getTransactions().get(0).getExtendedTransaction().getTransactionAmount().getAmount()).isNull();
        assertThat(account.getTransactions().get(1).getAmount()).isEqualTo("0.01");
        assertThat(account.getTransactions().get(1).getStatus()).isNull();
        assertThat(account.getTransactions().get(1).getType()).isEqualTo(ProviderTransactionType.CREDIT);
        assertThat(account.getTransactions().get(1).getExtendedTransaction().getTransactionAmount().getCurrency()).isNull();
        assertThat(account.getTransactions().get(1).getExtendedTransaction().getTransactionAmount().getAmount()).isEqualTo("0.01");
        assertThat(account.getTransactions().get(2).getAmount()).isEqualTo("15.13");
        assertThat(account.getTransactions().get(2).getStatus()).isEqualTo(TransactionStatus.BOOKED);
        assertThat(account.getTransactions().get(2).getType()).isEqualTo(ProviderTransactionType.DEBIT);
        assertThat(account.getTransactions().get(2).getExtendedTransaction().getTransactionAmount().getCurrency()).isEqualTo(CurrencyCode.GBP);
        assertThat(account.getTransactions().get(2).getExtendedTransaction().getTransactionAmount().getAmount()).isEqualTo("-15.13");
    }

}
