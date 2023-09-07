package com.yolt.providers.openbanking.ais.monzogroup.monzo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yolt.providers.common.domain.authenticationmeans.keymaterial.KeyRequirements;
import com.yolt.providers.common.domain.consenttesting.ConsentValidityRules;
import com.yolt.providers.openbanking.ais.common.HsmUtils;
import com.yolt.providers.openbanking.ais.generic2.DefaultProperties;
import com.yolt.providers.openbanking.ais.generic2.auth.DefaultAuthMeans;
import com.yolt.providers.openbanking.ais.generic2.claims.producer.DefaultJwtClaimsProducer;
import com.yolt.providers.openbanking.ais.generic2.claims.producer.ExpiringJwtClaimsProducerDecorator;
import com.yolt.providers.openbanking.ais.generic2.claims.token.DefaultTokenClaimsProducer;
import com.yolt.providers.openbanking.ais.generic2.claims.token.TokenClaimsProducer;
import com.yolt.providers.openbanking.ais.generic2.common.OpenBankingTokenScope;
import com.yolt.providers.openbanking.ais.generic2.common.ProviderIdentification;
import com.yolt.providers.openbanking.ais.generic2.domain.TokenScope;
import com.yolt.providers.openbanking.ais.generic2.http.HttpClientFactory;
import com.yolt.providers.openbanking.ais.generic2.service.AuthenticationService;
import com.yolt.providers.openbanking.ais.generic2.service.DefaultAuthenticationService;
import com.yolt.providers.openbanking.ais.generic2.service.ais.accountaccessconsentrequestservice.DefaultAccountAccessConsentRequestService;
import com.yolt.providers.openbanking.ais.generic2.service.ais.accountrequestservice.AccountRequestService;
import com.yolt.providers.openbanking.ais.generic2.service.ais.fetchdataservice.DefaultAccountFilter;
import com.yolt.providers.openbanking.ais.generic2.service.ais.fetchdataservice.DefaultFetchDataService;
import com.yolt.providers.openbanking.ais.generic2.service.ais.fetchdataservice.supportedaccounts.DefaultSupportedAccountsSupplier;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.account.*;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.account.schemesupport.DefaultSupportedSchemeAccountFilter;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.accountrefferencetypemapper.DefaultAccountReferenceTypeMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.amount.DefaultAmountParser;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.balance.DefaultBalanceMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.balanceamount.BalanceAmountMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.balanceamount.DefaultBalanceAmountMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.balancetype.DefaultBalanceTypeMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.creditcard.CreditCardMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.currency.DefaultCurrencyMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.extendedaccount.DefaultExtendedAccountMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.extendedbalance.DefaultExtendedBalancesMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.oauthtoken.AccessMeansMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.oauthtoken.DefaultAccessMeansMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.period.DefaultPeriodMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.scheme.DefaultSchemeMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.time.DefaultDateTimeMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.transaction.DefaultDirectDebitMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.transaction.DefaultStandingOrderMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.transactionstatus.DefaultTransactionStatusMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.transactionstatus.PendingAsNullTransactionStatusMapper;
import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.transactiontype.DefaultTransactionTypeMapper;
import com.yolt.providers.openbanking.ais.generic2.signer.ExternalUserRequestTokenSigner;
import com.yolt.providers.openbanking.ais.generic2.signer.PaymentRequestSigner;
import com.yolt.providers.openbanking.ais.monzogroup.common.MonzoGroupBaseDataProvider;
import com.yolt.providers.openbanking.ais.monzogroup.common.MonzoSupplementaryDataDeserializer;
import com.yolt.providers.openbanking.ais.monzogroup.common.auth.MonzoGroupAuthMeansMapper;
import com.yolt.providers.openbanking.ais.monzogroup.common.http.MonzoGroupHttpClientFactoryV2;
import com.yolt.providers.openbanking.ais.monzogroup.common.http.paymenthttppayloadsigner.MonzoGroupExternalPaymentRequestSignerV2;
import com.yolt.providers.openbanking.ais.monzogroup.common.oauth2.MonzoMutualTlsOauth2Client;
import com.yolt.providers.openbanking.ais.monzogroup.common.service.MonzoGroupRegistrationServiceV2;
import com.yolt.providers.openbanking.ais.monzogroup.common.service.ais.extendedtransactionmapper.MonzoExtendedTransactionMapperV3;
import com.yolt.providers.openbanking.ais.monzogroup.common.service.ais.fetchdataservice.MonzoGroupFetchDataServiceV4;
import com.yolt.providers.openbanking.ais.monzogroup.common.service.ais.potmapper.MonzoGroupPotMapperV2;
import com.yolt.providers.openbanking.ais.monzogroup.common.service.ais.transcationmapper.MonzoTransactionMapperV3;
import com.yolt.providers.openbanking.ais.monzogroup.common.service.restclient.MonzoGroupAisRestClientV3;
import com.yolt.providers.openbanking.ais.monzogroup.common.service.restclient.MonzoGroupRegistrationRestClientV2;
import com.yolt.providers.openbanking.dto.ais.openbanking316.OBReadDirectDebit2DataDirectDebit;
import com.yolt.providers.openbanking.dto.ais.openbanking316.OBStandingOrder6;
import com.yolt.providers.openbanking.dto.ais.openbanking316.OBSupplementaryData1;
import com.yolt.providers.openbanking.dto.ais.openbanking316.OBTransaction6;
import io.micrometer.core.instrument.MeterRegistry;
import nl.ing.lovebird.providerdomain.DirectDebitDTO;
import nl.ing.lovebird.providerdomain.ProviderTransactionDTO;
import nl.ing.lovebird.providerdomain.StandingOrderDTO;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.yolt.providers.common.versioning.ProviderVersion.VERSION_5;
import static com.yolt.providers.openbanking.ais.monzogroup.common.auth.MonzoGroupAuthMeansMapper.*;
import static com.yolt.providers.openbanking.dto.ais.openbanking316.OBBalanceType1Code.INTERIMAVAILABLE;

@Configuration
public class MonzoDataProviderV5BeanConfig {

    private static final String ENDPOINT_VERSION = "/v3.1";
    private static final String PROVIDER_KEY = "MONZO";
    private static final String DISPLAY_NAME = "Monzo";
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/London");
    private static final String ACCOUNT_NAME_FALLBACK = "Monzo Account";
    private static final String SIGNING_ALGORITHM = AlgorithmIdentifiers.RSA_PSS_USING_SHA256;
    private static final Duration CONSENT_WINDOW_DURATION = Duration.ofMinutes(5);

    @Bean("MonzoDataProviderV5")
    public MonzoGroupBaseDataProvider getMonzoDataProviderV5(MonzoPropertiesV2 properties,
                                                             MeterRegistry registry,
                                                             Clock clock,
                                                             @Qualifier("OpenBanking") ObjectMapper objectMapper) {
        ObjectMapper monzoObjectMapper = getMonzoObjectMapper(objectMapper);
        DefaultFetchDataService fetchDataService = getFetchDataService(monzoObjectMapper, clock, properties);
        AuthenticationService authenticationService = getAuthenticationService(properties, clock);
        AccountRequestService accountRequestService = getAccountRequestService(monzoObjectMapper, authenticationService);
        HttpClientFactory httpClientFactory = new MonzoGroupHttpClientFactoryV2(properties, registry, monzoObjectMapper);
        ProviderIdentification providerIdentification = new ProviderIdentification(PROVIDER_KEY, DISPLAY_NAME, VERSION_5);
        MonzoGroupAuthMeansMapper authMeansMapper = new MonzoGroupAuthMeansMapper();
        AccessMeansMapper accessMeansMapper = new DefaultAccessMeansMapper(monzoObjectMapper);
        Supplier<Optional<KeyRequirements>> signingKeyRequirements = () -> HsmUtils.getKeyRequirements(SIGNING_PRIVATE_KEY_ID_NAME);
        Supplier<Optional<KeyRequirements>> transportKeyRequirements = () -> HsmUtils.getKeyRequirements(TRANSPORT_PRIVATE_KEY_ID_NAME, TRANSPORT_CERTIFICATE_NAME);
        Supplier<ConsentValidityRules> consentValidityRulesSupplier = () -> new ConsentValidityRules(Collections.emptySet());
        MonzoGroupRegistrationServiceV2 registrationService = new MonzoGroupRegistrationServiceV2(getRestRegistrationClient(monzoObjectMapper), properties);

        return new MonzoGroupBaseDataProvider(fetchDataService, accountRequestService, authenticationService, httpClientFactory,
                getScope(), providerIdentification, authMeansMapper.getAuthMeansMapper(PROVIDER_KEY), authMeansMapper::getTypedAuthMeans,
                accessMeansMapper, signingKeyRequirements, transportKeyRequirements, consentValidityRulesSupplier, registrationService);
    }

    private ObjectMapper getMonzoObjectMapper(ObjectMapper obObjectMapper) {
        ObjectMapper mapper = obObjectMapper.copy();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(OBSupplementaryData1.class, new MonzoSupplementaryDataDeserializer(obObjectMapper));
        mapper.registerModule(module);
        return mapper;
    }

    private AccountRequestService getAccountRequestService(ObjectMapper objectMapper,
                                                           AuthenticationService authenticationService) {
        return new DefaultAccountAccessConsentRequestService(authenticationService, getRestClient(objectMapper), ENDPOINT_VERSION);
    }

    private AuthenticationService getAuthenticationService(DefaultProperties properties, Clock clock) {
        return new DefaultAuthenticationService(properties.getOAuthAuthorizationUrl(),
                new MonzoMutualTlsOauth2Client(properties, false),
                new ExternalUserRequestTokenSigner(SIGNING_ALGORITHM),
                getTokenClaimsProducer(properties),
                clock);
    }

    private DefaultFetchDataService getFetchDataService(ObjectMapper objectMapper,
                                                        Clock clock,
                                                        DefaultProperties properties) {
        return new MonzoGroupFetchDataServiceV4(getRestClient(objectMapper), properties, getTransactionMapper(), getDirectDebitMapper(),
                getStandingOrderMapper(), getAccountMapper(clock), new DefaultAccountFilter(),
                new DefaultSupportedAccountsSupplier(), CONSENT_WINDOW_DURATION, ENDPOINT_VERSION, new MonzoGroupPotMapperV2(clock), clock);
    }

    private MonzoGroupAisRestClientV3 getRestClient(ObjectMapper objectMapper) {
        return new MonzoGroupAisRestClientV3(getPaymentRequestSigner(objectMapper));
    }

    private MonzoGroupRegistrationRestClientV2 getRestRegistrationClient(ObjectMapper objectMapper) {
        return new MonzoGroupRegistrationRestClientV2(getPaymentRequestSigner(objectMapper));
    }

    private TokenClaimsProducer getTokenClaimsProducer(DefaultProperties properties) {
        return new DefaultTokenClaimsProducer(new ExpiringJwtClaimsProducerDecorator(
                new DefaultJwtClaimsProducer(DefaultAuthMeans::getClientId, properties.getAudience()), 60));
    }

    private PaymentRequestSigner getPaymentRequestSigner(ObjectMapper objectMapper) {
        return new MonzoGroupExternalPaymentRequestSignerV2(objectMapper, SIGNING_ALGORITHM);
    }

    private Function<OBTransaction6, ProviderTransactionDTO> getTransactionMapper() {
        DefaultDateTimeMapper zonedDateTimeMapper = new DefaultDateTimeMapper(ZONE_ID);
        return new MonzoTransactionMapperV3(
                new MonzoExtendedTransactionMapperV3(
                        new DefaultAccountReferenceTypeMapper(),
                        new DefaultTransactionStatusMapper(),
                        getBalanceAmountMapper(),
                        false,
                        ZONE_ID),
                zonedDateTimeMapper,
                new PendingAsNullTransactionStatusMapper(),
                new DefaultAmountParser(),
                new DefaultTransactionTypeMapper());
    }

    private Function<OBReadDirectDebit2DataDirectDebit, Optional<DirectDebitDTO>> getDirectDebitMapper() {
        return new DefaultDirectDebitMapper(ZONE_ID, new DefaultAmountParser());
    }

    private Function<OBStandingOrder6, StandingOrderDTO> getStandingOrderMapper() {
        return new DefaultStandingOrderMapper(new DefaultPeriodMapper(),
                new DefaultAmountParser(),
                new DefaultSchemeMapper(),
                new DefaultDateTimeMapper(ZONE_ID));
    }

    private DefaultAccountMapper getAccountMapper(Clock clock) {
        return new DefaultAccountMapper(
                () -> Arrays.asList(INTERIMAVAILABLE),
                () -> Arrays.asList(INTERIMAVAILABLE),
                new DefaultCurrencyMapper(),
                new DefaultAccountIdMapper(),
                new DefaultAccountTypeMapper(),
                new CreditCardMapper(),
                new AccountNumberMapper(new DefaultSchemeMapper()),
                new DefaultAccountNameMapper(account -> ACCOUNT_NAME_FALLBACK),
                new DefaultBalanceMapper(),
                new DefaultExtendedAccountMapper(
                        new DefaultAccountReferenceTypeMapper(),
                        new DefaultCurrencyMapper(),
                        new DefaultExtendedBalancesMapper(
                                getBalanceAmountMapper(),
                                new DefaultBalanceTypeMapper(),
                                ZONE_ID)),
                new DefaultSupportedSchemeAccountFilter(),
                clock);
    }

    private BalanceAmountMapper getBalanceAmountMapper() {
        return new DefaultBalanceAmountMapper(new DefaultCurrencyMapper(), new DefaultBalanceMapper());
    }

    private TokenScope getScope() {
        return TokenScope.builder()
                .grantScope(OpenBankingTokenScope.ACCOUNTS.getGrantScope())
                .authorizationUrlScope(OpenBankingTokenScope.ACCOUNTS.getAuthorizationUrlScope())
                .build();
    }
}
