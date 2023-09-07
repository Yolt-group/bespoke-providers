package com.yolt.providers.redsys.common.service.mapper;

import lombok.NoArgsConstructor;
import nl.ing.lovebird.extendeddata.common.CurrencyCode;

@NoArgsConstructor
public class CurrencyCodeMapperV1 implements CurrencyCodeMapper {

    @Override
    public CurrencyCode toCurrencyCode(final String currencyCode) {
        try {
            return currencyCode == null ? null : CurrencyCode.valueOf(currencyCode);
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
}
