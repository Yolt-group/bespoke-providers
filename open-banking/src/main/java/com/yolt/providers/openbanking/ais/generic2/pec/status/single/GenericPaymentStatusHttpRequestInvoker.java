package com.yolt.providers.openbanking.ais.generic2.pec.status.single;

import com.fasterxml.jackson.databind.JsonNode;
import com.yolt.providers.common.exception.TokenInvalidException;
import com.yolt.providers.common.pis.paymentexecutioncontext.http.PaymentHttpRequestInvoker;
import com.yolt.providers.openbanking.ais.generic2.common.EndpointsVersionable;
import com.yolt.providers.openbanking.ais.generic2.common.ProviderIdentification;
import com.yolt.providers.openbanking.ais.generic2.http.HttpClientFactory;
import com.yolt.providers.openbanking.ais.generic2.pec.common.exception.GenericPaymentRequestInvocationException;
import com.yolt.providers.openbanking.ais.generic2.pec.restclient.PisRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class GenericPaymentStatusHttpRequestInvoker implements PaymentHttpRequestInvoker<Void, GenericPaymentStatusPreExecutionResult> {

    private static final String DOMESTIC_PAYMENTS_STATUS_PATH = "/pisp/domestic-payments/%s";
    private static final String DOMESTIC_PAYMENT_CONSENTS_STATUS_PATH = "/pisp/domestic-payment-consents/%s";

    private final HttpClientFactory httpClientFactory;
    private final PisRestClient restClient;
    private final EndpointsVersionable endpointsVersionable;
    private final ProviderIdentification providerIdentification;

    @Override
    public ResponseEntity<JsonNode> invokeRequest(HttpEntity<Void> httpEntity, GenericPaymentStatusPreExecutionResult preExecutionResult) {
        var httpClient = httpClientFactory.createHttpClient(preExecutionResult.getRestTemplateManager(), preExecutionResult.getAuthMeans(), providerIdentification.getDisplayName());

        try {
            if (!StringUtils.isEmpty(preExecutionResult.getPaymentId())) {
                return restClient.getPaymentStatus(httpClient, endpointsVersionable.getAdjustedUrlPath(String.format(DOMESTIC_PAYMENTS_STATUS_PATH, preExecutionResult.getPaymentId())), httpEntity);
            } else {
                return restClient.getConsentStatus(httpClient, endpointsVersionable.getAdjustedUrlPath(String.format(DOMESTIC_PAYMENT_CONSENTS_STATUS_PATH, preExecutionResult.getConsentId())), httpEntity);
            }
        } catch (TokenInvalidException ex) {
            throw new GenericPaymentRequestInvocationException(ex);
        }
    }
}
