package com.yolt.providers.ing.common.pec.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.yolt.providers.common.cryptography.RestTemplateManager;
import com.yolt.providers.common.exception.TokenInvalidException;
import com.yolt.providers.common.pis.paymentexecutioncontext.http.PaymentHttpRequestInvoker;
import com.yolt.providers.common.rest.http.HttpClient;
import com.yolt.providers.common.rest.http.HttpErrorHandler;
import com.yolt.providers.common.rest.http.ProviderClientEndpoints;
import com.yolt.providers.ing.common.auth.IngAuthenticationMeans;
import com.yolt.providers.ing.common.http.HttpClientFactory;
import com.yolt.providers.ing.common.pec.PaymentEndpointResolver;
import com.yolt.providers.ing.common.pec.submit.DefaultSubmitPaymentPreExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import static com.yolt.providers.ing.common.pec.IngPecConstants.GET_PAYMENT_STATUS_HTTP_METHOD;

@RequiredArgsConstructor
public class DefaultStatusPaymentHttpRequestInvokerV2 implements PaymentHttpRequestInvoker<Void, DefaultSubmitPaymentPreExecutionResult> {

    private final HttpClientFactory httpClientFactory;
    private final HttpErrorHandler httpErrorHandler;
    private final String providerIdentifier;
    private final PaymentEndpointResolver endpointResolver;

    @Override
    @SneakyThrows(TokenInvalidException.class)
    public ResponseEntity<JsonNode> invokeRequest(final HttpEntity<Void> httpEntity, final DefaultSubmitPaymentPreExecutionResult preExecutionResult) {
        IngAuthenticationMeans authMeans = preExecutionResult.getAuthenticationMeans();
        RestTemplateManager restTemplateManager = preExecutionResult.getRestTemplateManager();

        String expandedEndpointPath = String.format(
                endpointResolver.getStatusPaymentEndpoint(preExecutionResult.getPaymentType()),
                preExecutionResult.getPaymentId());

        HttpClient httpClient = httpClientFactory.createPisHttpClient(authMeans, restTemplateManager, providerIdentifier);
        return httpClient.exchange(
                expandedEndpointPath,
                GET_PAYMENT_STATUS_HTTP_METHOD,
                httpEntity,
                ProviderClientEndpoints.GET_PAYMENT_STATUS,
                JsonNode.class,
                httpErrorHandler);
    }
}
