package com.yolt.providers.n26.common.dto.ais.fetchdata;

import org.springframework.data.web.JsonPath;
import org.springframework.data.web.ProjectedPayload;

import java.util.List;

@ProjectedPayload
public interface AccountsResponse {

    @JsonPath("$.accounts")
    List<Account> getAccounts();
}
