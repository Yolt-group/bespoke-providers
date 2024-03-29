package com.yolt.providers.commerzbankgroup.common.api.dto.fetchdata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * The transaction status is filled with codes of the ISO 20022 data table: - 'ACCC': 'AcceptedSettlementCompleted' -   Settlement on the creditor's account has been completed. - 'ACCP': 'AcceptedCustomerProfile' -    Preceding check of technical validation was successful.    Customer profile check was also successful. - 'ACSC': 'AcceptedSettlementCompleted' -    Settlement on the debtor�s account has been completed.      **Usage:** this can be used by the first agent to report to the debtor that the transaction has been completed.       **Warning:** this status is provided for transaction status reasons, not for financial information.    It can only be used after bilateral agreement. - 'ACSP': 'AcceptedSettlementInProcess' -    All preceding checks such as technical validation and customer profile were successful and therefore the payment initiation has been accepted for execution. - 'ACTC': 'AcceptedTechnicalValidation' -    Authentication and syntactical and semantical validation are successful. - 'ACWC': 'AcceptedWithChange' -    Instruction is accepted but a change will be made, such as date or remittance not sent. - 'ACWP': 'AcceptedWithoutPosting' -    Payment instruction included in the credit transfer is accepted without being posted to the creditor customer�s account. - 'RCVD': 'Received' -    Payment initiation has been received by the receiving agent. - 'PDNG': 'Pending' -    Payment initiation or individual transaction included in the payment initiation is pending.    Further checks and status update will be performed. - 'RJCT': 'Rejected' -    Payment initiation or individual transaction included in the payment initiation has been rejected. - 'CANC': 'Cancelled'   Payment initiation has been cancelled before execution   Remark: This codeis accepted as new code by ISO20022. - 'ACFC': 'AcceptedFundsChecked' -   Preceding check of technical validation and customer profile was successful and an automatic funds check was positive .   Remark: This code is accepted as new code by ISO20022. - 'PATC': 'PartiallyAcceptedTechnical'   Correct The payment initiation needs multiple authentications, where some but not yet all have been performed. Syntactical and semantical validations are successful.   Remark: This code is accepted as new code by ISO20022. - 'PART': 'PartiallyAccepted' -   A number of transactions have been accepted, whereas another number of transactions have not yet achieved 'accepted' status.   Remark: This code may be used only in case of bulk payments. It is only used in a situation where all mandated authorisations have been applied, but some payments have been rejected.    
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-06-10T11:00:09.615090+02:00[Europe/Warsaw]")
public enum TransactionStatus {
  
  ACCC("ACCC"),
  
  ACCP("ACCP"),
  
  ACSC("ACSC"),
  
  ACSP("ACSP"),
  
  ACTC("ACTC"),
  
  ACWC("ACWC"),
  
  ACWP("ACWP"),
  
  RCVD("RCVD"),
  
  PDNG("PDNG"),
  
  RJCT("RJCT"),
  
  CANC("CANC"),
  
  ACFC("ACFC"),
  
  PATC("PATC"),
  
  PART("PART");

  private String value;

  TransactionStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static TransactionStatus fromValue(String value) {
    for (TransactionStatus b : TransactionStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

