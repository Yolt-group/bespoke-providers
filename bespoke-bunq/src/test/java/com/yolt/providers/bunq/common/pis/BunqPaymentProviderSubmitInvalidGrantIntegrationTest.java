package com.yolt.providers.bunq.common.pis;

import com.bunq.sdk.security.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolt.providers.bunq.AuthMeans;
import com.yolt.providers.bunq.RestTemplateManagerMock;
import com.yolt.providers.bunq.TestApp;
import com.yolt.providers.bunq.common.BunqPaymentProvider;
import com.yolt.providers.bunq.common.pis.pec.PaymentProviderState;
import com.yolt.providers.bunq.common.pis.pec.exception.PreExecutionMapperException;
import com.yolt.providers.bunq.common.pis.pec.exception.ProviderStateSerializationException;
import com.yolt.providers.common.cryptography.Signer;
import com.yolt.providers.common.exception.TokenInvalidException;
import com.yolt.providers.common.pis.common.PaymentType;
import com.yolt.providers.common.pis.paymentexecutioncontext.exception.PaymentExecutionTechnicalException;
import com.yolt.providers.common.pis.sepa.SubmitPaymentRequestBuilder;
import com.yolt.providers.common.rest.ExternalRestTemplateBuilderFactory;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(classes = TestApp.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureWireMock(stubs = "classpath:/mappings/pis/401-submit-invalidgrant", httpsPort = 0, port = 0)
class BunqPaymentProviderSubmitInvalidGrantIntegrationTest {

    private static final String SESSION_TOKEN = "a3cc2d56b7d30dae471c418b16cd15d674aa06ffa9f45beae39dbb9a8ac749c9";

    @Autowired
    private BunqPaymentProvider bunqPaymentProvider;

    @Autowired
    private ExternalRestTemplateBuilderFactory externalRestTemplateBuilderFactory;

    private RestTemplateManagerMock restTemplateManagerMock;

    @Autowired
    @Qualifier("BunqObjectMapper")
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {
        objectMapper = new ObjectMapper();
        restTemplateManagerMock = new RestTemplateManagerMock(externalRestTemplateBuilderFactory);
    }

    @Mock
    private Signer signer;

    @Test
    void shouldReturnPaymentExecutionTechnicalExceptionWhenInvalidGrantIsReceivedFromBank() {
        // given
        var authMeans = AuthMeans.prepareAuthMeansV2();
        var keyPair = SecurityUtils.generateKeyPair();
        var providerState = new PaymentProviderState(12345, PaymentType.SINGLE, SESSION_TOKEN, Instant.now().plusSeconds(600L).toEpochMilli(), SecurityUtils.getPublicKeyFormattedString(keyPair), SecurityUtils.getPrivateKeyFormattedString(keyPair));
        var submitPaymentRequest = new SubmitPaymentRequestBuilder()
                .setProviderState(serializeProviderState(providerState))
                .setAuthenticationMeans(authMeans)
                .setRedirectUrlPostedBackFromSite("https://www.yolt.com/callback?code=someCode&state=randomState")
                .setSigner(signer)
                .setRestTemplateManager(restTemplateManagerMock)
                .setPsuIpAddress("fakePsuIpAddress")
                .build();

        //when
        ThrowableAssert.ThrowingCallable call = () -> bunqPaymentProvider.submitPayment(submitPaymentRequest);

        //then
        assertThatExceptionOfType(PaymentExecutionTechnicalException.class)
                .isThrownBy(call)
                .withMessage("submit_preparation_error")
                .satisfies(ex -> {
                    assertThat(ex.getCause()).isInstanceOf(PreExecutionMapperException.class);
                    assertThat(ex.getCause().getMessage()).isEqualTo("Error occurred during preparing PreExecutionResult");
                    assertThat(ex.getCause().getCause()).isInstanceOf(TokenInvalidException.class);
                    assertThat(ex.getCause().getCause().getMessage()).isEqualTo("We are not authorized to call endpoint: HTTP 401");
                });
    }

    private String serializeProviderState(PaymentProviderState providerState) {
        try {
            return objectMapper.writeValueAsString(providerState);
        } catch (JsonProcessingException e) {
            throw new ProviderStateSerializationException("Cannot serialize provider state", e);
        }
    }
}

