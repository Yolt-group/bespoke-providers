package com.yolt.providers.redsys.santander;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolt.providers.common.versioning.ProviderVersion;
import com.yolt.providers.redsys.common.RedsysDataProviderV2;
import com.yolt.providers.redsys.common.service.RedsysAuthorizationService;
import com.yolt.providers.redsys.common.service.RedsysFetchDataServiceV2;

import java.time.Clock;

import static com.yolt.providers.common.versioning.ProviderVersion.VERSION_3;

public class SantanderESDataProviderV3 extends RedsysDataProviderV2 {
    public static final String PROVIDER_IDENTIFIER = "SANTANDER_ES";
    public static final String PROVIDER_IDENTIFIER_NAME = "Santander ES Bank";

    public SantanderESDataProviderV3(final SantanderESProperties properties,
                                     final RedsysAuthorizationService authorizationService,
                                     final RedsysFetchDataServiceV2 fetchDataService,
                                     final ObjectMapper mapper,
                                     Clock clock) {
        super(properties, authorizationService, fetchDataService, mapper, clock);
    }

    @Override
    public String getProviderIdentifier() {
        return PROVIDER_IDENTIFIER;
    }

    @Override
    public String getProviderIdentifierDisplayName() {
        return PROVIDER_IDENTIFIER_NAME;
    }

    @Override
    public ProviderVersion getVersion() {
        return VERSION_3;
    }
}
