package com.example.statementservice.rest;

import com.example.statementservice.model.GeneratePdfRequest;
import com.example.statementservice.model.GeneratePdfResponse;
import com.example.statementservice.model.Transaction;
import com.example.statementservice.model.TransactionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestClient {
    @Value("${template-id.account-statement}")
    private String ACCOUNT_STATEMENT;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Transaction> getAllTransactions(String accountNumber, String fromDate, String toDate) {
        List<Transaction> allTransactions = new ArrayList<>();
        int currentPage = 1;
        boolean lastPage = false;
        while (!lastPage) {
            TransactionResponse response;
                response = restTemplate.getForObject(
                        "http://localhost:8080/api/v1/transactions?accountNumber={acc}&fromDate={from}&toDate={to}&pageNo={page}",
                        TransactionResponse.class,
                        accountNumber, fromDate, toDate, currentPage);
            if (response != null && !response.transactions().isEmpty()) {
                allTransactions.addAll(response.transactions());
                lastPage = response.page().lastPage();
                currentPage++;
            }
        }
        return allTransactions;
    }

    public String generatePdf(List<Transaction> transactions) {
        GeneratePdfRequest request = new GeneratePdfRequest(ACCOUNT_STATEMENT, transactions);
        GeneratePdfResponse response = restTemplate.postForObject(
                "http://localhost:8080/api/v1/pdf",
                request,
                GeneratePdfResponse.class);
        return response != null ? response.data() : "";
    }
}