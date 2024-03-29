package com.yolt.providers.rabobank.dto.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Account status. The value is one of the following:    - \"enabled\": account is available    - \"deleted\": account is terminated    - \"blocked\": account is blocked e.g. for legal reasons  If this field is not used, than the account is available in the sense of this specification.
 */
public enum AccountStatus {

    ENABLED("enabled"),

    DELETED("deleted"),

    BLOCKED("blocked");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AccountStatus fromValue(String text) {
        for (AccountStatus b : AccountStatus.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}

