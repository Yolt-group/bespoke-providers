package com.yolt.providers.openbanking.ais.sainsburys.auth;

import com.yolt.providers.common.domain.authenticationmeans.BasicAuthenticationMean;
import com.yolt.providers.common.domain.authenticationmeans.TypedAuthenticationMeans;
import com.yolt.providers.common.exception.InvalidAuthenticationMeansException;
import com.yolt.providers.common.util.KeyUtil;
import com.yolt.providers.openbanking.ais.generic2.auth.DefaultAuthMeans;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class SainsburysAuthMeansMapperV2 {

    public static final String INSTITUTION_ID_NAME = "institution-id";
    public static final String CLIENT_ID_NAME = "client-id";
    public static final String SIGNING_KEY_HEADER_ID_NAME = "signing-private-key-header-id";
    public static final String SIGNING_PRIVATE_KEY_ID_NAME = "signing-private-key-id";
    public static final String TRANSPORT_CERTIFICATE_NAME = "transport-certificate";
    public static final String TRANSPORT_PRIVATE_KEY_ID_NAME = "transport-private-key-id";
    public static final String ORGANIZATION_ID_NAME = "organization-id";
    public static final String SOFTWARE_ID_NAME = "software-id";
    public static final String SOFTWARE_STATEMENT_ASSERTION_NAME = "software-statement-assertion";

    public Function<Map<String, BasicAuthenticationMean>, DefaultAuthMeans> getAuthMeansMapper(String providerKey) {
        return typedAuthenticationMeans -> mapToDefaultAuthMeans(typedAuthenticationMeans, providerKey);
    }

    public Map<String, TypedAuthenticationMeans> getTypedAuthMeans() {
        Map<String, TypedAuthenticationMeans> typedAuthenticationMeans = new HashMap<>();
        typedAuthenticationMeans.put(INSTITUTION_ID_NAME, TypedAuthenticationMeans.INSTITUTION_ID_STRING);
        typedAuthenticationMeans.put(CLIENT_ID_NAME, TypedAuthenticationMeans.CLIENT_ID_STRING);
        typedAuthenticationMeans.put(SIGNING_KEY_HEADER_ID_NAME, TypedAuthenticationMeans.SIGNING_KEY_ID_STRING);
        typedAuthenticationMeans.put(SIGNING_PRIVATE_KEY_ID_NAME, TypedAuthenticationMeans.KEY_ID);
        typedAuthenticationMeans.put(TRANSPORT_CERTIFICATE_NAME, TypedAuthenticationMeans.CERTIFICATE_PEM);
        typedAuthenticationMeans.put(TRANSPORT_PRIVATE_KEY_ID_NAME, TypedAuthenticationMeans.KEY_ID);
        typedAuthenticationMeans.put(ORGANIZATION_ID_NAME, TypedAuthenticationMeans.ORGANIZATION_ID_STRING);
        typedAuthenticationMeans.put(SOFTWARE_ID_NAME, TypedAuthenticationMeans.SOFTWARE_ID_STRING);
        typedAuthenticationMeans.put(SOFTWARE_STATEMENT_ASSERTION_NAME, TypedAuthenticationMeans.SOFTWARE_STATEMENT_ASSERTION_STRING);
        return typedAuthenticationMeans;
    }

    private DefaultAuthMeans mapToDefaultAuthMeans(Map<String, BasicAuthenticationMean> typedAuthenticationMeans,
                                                   String providerIdentifier) {
        String clientId = typedAuthenticationMeans.containsKey(CLIENT_ID_NAME) ? typedAuthenticationMeans.get(CLIENT_ID_NAME).getValue() : null;
        return DefaultAuthMeans.builder()
                .institutionId(typedAuthenticationMeans.get(INSTITUTION_ID_NAME).getValue())
                .clientId(clientId)
                .signingKeyIdHeader(typedAuthenticationMeans.get(SIGNING_KEY_HEADER_ID_NAME).getValue())
                .signingPrivateKeyId(UUID.fromString(typedAuthenticationMeans.get(SIGNING_PRIVATE_KEY_ID_NAME).getValue()))
                .transportCertificate(createCertificate(typedAuthenticationMeans.get(TRANSPORT_CERTIFICATE_NAME).getValue(), providerIdentifier))
                .transportPrivateKeyId(UUID.fromString(typedAuthenticationMeans.get(TRANSPORT_PRIVATE_KEY_ID_NAME).getValue()))
                .organizationId(typedAuthenticationMeans.get(ORGANIZATION_ID_NAME).getValue())
                .softwareId(typedAuthenticationMeans.get(SOFTWARE_ID_NAME).getValue())
                .certificateId(typedAuthenticationMeans.get(SIGNING_KEY_HEADER_ID_NAME).getValue())
                .build();
    }

    private X509Certificate createCertificate(String certificate, String providerIdentifier) {
        try {
            return KeyUtil.createCertificateFromPemFormat(certificate);
        } catch (CertificateException e) {
            throw new InvalidAuthenticationMeansException(providerIdentifier,
                    TRANSPORT_CERTIFICATE_NAME, "Cannot process certificate for thumbprint");
        }
    }
}