package com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.extendedbalance;

import com.yolt.providers.openbanking.ais.generic2.service.ais.mappers.balanceamount.BalanceAmountMapper;
import com.yolt.providers.openbanking.dto.ais.openbanking316.OBBalanceType1Code;
import com.yolt.providers.openbanking.dto.ais.openbanking316.OBReadBalance1DataBalance;
import lombok.AllArgsConstructor;
import nl.ing.lovebird.extendeddata.account.BalanceDTO;
import nl.ing.lovebird.extendeddata.account.BalanceType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DefaultExtendedBalancesMapper implements Function<List<OBReadBalance1DataBalance>, List<BalanceDTO>> {

    private final BalanceAmountMapper balanceAmountMapper;
    private final Function<OBBalanceType1Code, BalanceType> balanceTypeMapper;
    private final ZoneId zoneId;

    @Override
    public List<BalanceDTO> apply(List<OBReadBalance1DataBalance> balances) {
        return balances.stream()
                .map(balance -> {
                    BalanceDTO.BalanceDTOBuilder builder = BalanceDTO.builder()
                            .balanceType(balanceTypeMapper.apply(balance.getType()))
                            .balanceAmount(balanceAmountMapper.apply(balance));
                    if (balance.getDateTime() != null) {
                        builder.lastChangeDateTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(balance.getDateTime())).withZoneSameInstant(zoneId))
                                .referenceDate(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(balance.getDateTime())).withZoneSameInstant(zoneId));
                    }
                    return builder.build();
                })
                .filter(balance -> balance.getBalanceType() != null)
                .collect(Collectors.toList());
    }
}
