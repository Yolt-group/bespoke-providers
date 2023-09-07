package com.yolt.providers.monorepogroup.libragroup.common.ais.data.dto;

import org.springframework.data.web.JsonPath;
import org.springframework.data.web.ProjectedPayload;

import java.math.BigDecimal;
import java.time.LocalDate;

@ProjectedPayload
public interface Transaction {

    @JsonPath("$.transactionId")
    String getTransactionId();

    @JsonPath("$.bookingDate")
    LocalDate getBookingDate();

    @JsonPath("$.valueDate")
    LocalDate getValueDate();

    @JsonPath("$.creditorName")
    String getCreditorName();

    @JsonPath("$.creditorAccount.iban")
    String getCreditorIban();

    @JsonPath("$.debtorName")
    String getDebtorName();

    @JsonPath("$.debtorAccount.iban")
    String getDebtorIban();

    @JsonPath("$.remittanceInformationUnstructured")
    String getRemittanceInformationUnstructured();

    @JsonPath("$.transactionAmount.amount")
    BigDecimal getAmount();

    @JsonPath("$.transactionAmount.currency")
    String getCurrency();

}
