package com.yolt.providers.gruppocedacri.common.service;

import com.yolt.providers.common.ais.DataProviderResponse;
import com.yolt.providers.common.exception.ProviderFetchDataException;
import com.yolt.providers.common.exception.TokenInvalidException;
import com.yolt.providers.gruppocedacri.common.GruppoCedacriAccessMeans;
import com.yolt.providers.gruppocedacri.common.config.GruppoCedacriProperties;
import com.yolt.providers.gruppocedacri.common.dto.fetchdata.Account;
import com.yolt.providers.gruppocedacri.common.dto.fetchdata.Balance;
import com.yolt.providers.gruppocedacri.common.dto.fetchdata.Transaction;
import com.yolt.providers.gruppocedacri.common.dto.fetchdata.TransactionsResponse;
import com.yolt.providers.gruppocedacri.common.http.GruppoCedacriHttpClient;
import com.yolt.providers.gruppocedacri.common.mapper.GruppoCedacriAccountMapper;
import com.yolt.providers.gruppocedacri.common.mapper.GruppoCedacriTransactionMapper;
import com.yolt.providers.gruppocedacri.common.util.GruppoCedacriDateConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.ing.lovebird.extendeddata.transaction.TransactionStatus;
import nl.ing.lovebird.providerdomain.ProviderAccountDTO;
import nl.ing.lovebird.providerdomain.ProviderTransactionDTO;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GruppoCedacriFetchDataServiceV1 implements GruppoCedacriFetchDataService {

    private final GruppoCedacriAccountMapper accountMapper;
    private final GruppoCedacriTransactionMapper transactionMapper;
    private final GruppoCedacriDateConverter dateConverter;
    private final GruppoCedacriProperties properties;

    @Override
    public DataProviderResponse fetchData(GruppoCedacriHttpClient httpClient,
                                          GruppoCedacriAccessMeans accessMeans,
                                          Instant transactionFetchStart,
                                          String psuIpAddress,
                                          String providerDisplayName) throws TokenInvalidException, ProviderFetchDataException {
        List<ProviderAccountDTO> providerAccountsDTO = new ArrayList<>();

        for (Account account : httpClient.getAccounts(accessMeans, psuIpAddress).getAccounts()) {
            String accountId = account.getResourceId();
            try {
                List<Balance> balances = httpClient.getBalances(accessMeans, psuIpAddress, accountId).getBalances();

                List<ProviderTransactionDTO> transactions = new ArrayList<>();

                String dateFrom = dateConverter.toDateFormat(transactionFetchStart);
                TransactionsResponse transactionsResponse = httpClient.getTransactions(accessMeans, accountId, dateFrom, psuIpAddress);
                transactions.addAll(mapTransactions(transactionsResponse.getBookedTransactions(), TransactionStatus.BOOKED));
                int counter = 0;
                String nextPage = transactionsResponse.getNextHref();
                while (shouldFetchNextPage(nextPage, counter)) {
                    transactionsResponse = httpClient.getTransactionsNextPage(nextPage, accessMeans, psuIpAddress);
                    transactions.addAll(mapTransactions(transactionsResponse.getBookedTransactions(), TransactionStatus.BOOKED));
                    nextPage = transactionsResponse.getNextHref();
                    counter++;
                }

                ProviderAccountDTO providerAccountDTO = accountMapper.map(account, providerDisplayName, balances, transactions);
                providerAccountsDTO.add(providerAccountDTO);
            } catch (Exception e) {
                throw new ProviderFetchDataException(e);
            }
        }
        return new DataProviderResponse(providerAccountsDTO);
    }

    private List<ProviderTransactionDTO> mapTransactions(List<Transaction> bankTransactions, TransactionStatus status) {
        return bankTransactions
                .stream()
                .map(t -> transactionMapper.map(t, status))
                .collect(Collectors.toList());
    }

    private boolean shouldFetchNextPage(String nextHref, int counter) {
        return properties.getPaginationLimit() > counter
                && StringUtils.hasText(nextHref);
    }
}
