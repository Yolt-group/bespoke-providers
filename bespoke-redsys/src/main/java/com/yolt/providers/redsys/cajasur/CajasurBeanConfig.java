package com.yolt.providers.redsys.cajasur;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolt.providers.redsys.common.rest.BookingStatus;
import com.yolt.providers.redsys.common.rest.RedsysRestTemplateService;
import com.yolt.providers.redsys.common.service.*;
import com.yolt.providers.redsys.common.service.mapper.CurrencyCodeMapper;
import com.yolt.providers.redsys.common.service.mapper.CurrencyCodeMapperV1;
import com.yolt.providers.redsys.common.service.mapper.RedsysDataMapperServiceV3;
import com.yolt.providers.redsys.common.service.mapper.RedsysExtendedDataMapperV2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class CajasurBeanConfig {

    private static final ZoneId MADRID_ZONE_ID = ZoneId.of("Europe/Madrid");
    private static final String TRANSACTION_DESCRIPTION_FALLBACK = "Description not available";

    @Bean
    public CajasurDataProviderV1 getCajasurDataProvider(final CajasurProperties properties,
                                                 @Qualifier("Redsys") final ObjectMapper mapper,
                                                 Clock clock) {
        RedsysAuthorizationService authorizationService = getAuthorizationService(mapper, properties);
        RedsysFetchDataServiceV2 fetchDataService = getFetchDataServiceV1(mapper, properties, clock);
        return new CajasurDataProviderV1(properties, authorizationService, fetchDataService, mapper, clock);
    }

    private RedsysAuthorizationService getAuthorizationService(final ObjectMapper objectMapper,
                                                               final CajasurProperties properties) {
        return new RedsysAuthorizationService(new RedsysRestTemplateService(objectMapper, properties),
                new RedsysAllAccountsConsentObjectService(),
                properties);
    }

    private RedsysFetchDataServiceV3 getFetchDataServiceV1(final ObjectMapper objectMapper,
                                                           final CajasurProperties properties,
                                                           final Clock clock) {
        CurrencyCodeMapper currencyCodeMapper = new CurrencyCodeMapperV1();
        return new RedsysFetchDataServiceV3(new RedsysRestTemplateService(objectMapper, properties), properties,
                new RedsysDataMapperServiceV3(
                        currencyCodeMapper,
                        new RedsysExtendedDataMapperV2(currencyCodeMapper, MADRID_ZONE_ID),
                        clock,
                        TRANSACTION_DESCRIPTION_FALLBACK),
                BookingStatus.BOTH, new TransactionsFetchStartTimeNoLimited());
    }
}
