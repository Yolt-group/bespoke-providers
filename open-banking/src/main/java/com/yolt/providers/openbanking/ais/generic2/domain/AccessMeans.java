package com.yolt.providers.openbanking.ais.generic2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessMeans {

    private Instant created;

    private UUID userId;

    private String accessToken;

    private String refreshToken;

    private Date expireTime;

    private Date updated;

    private String redirectUri;

    /**
     *      
     * * * @deprecated  Constructor with 'created' parameter (generated by lombok) should be used.   
     */
    @Deprecated
    public AccessMeans(final UUID userId, final String accessToken, final String refreshToken, final Date expireTime, final Date updated, final String redirectUri) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expireTime = expireTime;
        this.updated = updated;
        this.redirectUri = redirectUri;
    }

    public Instant getCreated() {
        return created == null ? Instant.ofEpochMilli(0L) : created;
    }

    public boolean isInConsentWindow(Clock clock, Duration consentWindow) {
        return getCreated().isAfter(Instant.now(clock).minus(consentWindow));
    }
}