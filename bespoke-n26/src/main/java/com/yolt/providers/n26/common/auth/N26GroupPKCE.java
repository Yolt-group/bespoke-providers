package com.yolt.providers.n26.common.auth;

import com.yolt.securityutils.oauth2.OAuth2ProofKeyCodeExchange;
import lombok.NoArgsConstructor;

import java.security.*;
import java.util.Base64;

@NoArgsConstructor
public class N26GroupPKCE {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder();
    private static final String CODE_CHALLENGE_METHOD = "S256";

    public OAuth2ProofKeyCodeExchange createRandomS256() {
        byte[] verifier = new byte[43];
        RANDOM.nextBytes(verifier);
        return createRandomS256(verifier);
    }

    private OAuth2ProofKeyCodeExchange createRandomS256(byte[] verifier) {
        String codeVerifier = URL_ENCODER.encodeToString(verifier).replace("=", "");
        try {
            String codeChallenge = getCodeChallenge(codeVerifier.getBytes());
            return new OAuth2ProofKeyCodeExchange(codeVerifier, codeChallenge, CODE_CHALLENGE_METHOD);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to generate a SHA-256 challenge for PKCE", e);
        }
    }

    private String getCodeChallenge(byte[] codeVerifier) throws NoSuchAlgorithmException {
        return URL_ENCODER.encodeToString(getChallenge(codeVerifier)).replace("=", "");
    }

    private byte[] getChallenge(byte[] codeVerifier) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(codeVerifier);
    }
}
