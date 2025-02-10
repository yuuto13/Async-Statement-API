package com.example.statementservice.service;

import com.example.statementservice.cache.TaskCache;
import com.example.statementservice.controller.MockCoreBankingController;
import com.example.statementservice.model.GeneratePdfRequest;
import com.example.statementservice.model.GeneratePdfResponse;
import com.example.statementservice.model.TaskResult;
import com.example.statementservice.model.TaskStatus;
import com.example.statementservice.model.Transaction;
import com.example.statementservice.model.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class StatementService {
    @Value("${template-id.account-statement}")
    private String ACCOUNT_STATEMENT;

    private final Logger logger = Logger.getLogger(StatementService.class.getName());

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private MockCoreBankingController mockCoreBankingController;

    @Autowired
    private TaskCache taskCache;

    public void generateStatementAsync(String taskId, String fromDate, String toDate, String accountNumber) {
        try {
            List<Transaction> transactions = getAllTransactions(accountNumber, fromDate, toDate);
            String pdf = generatePdf(transactions);
            taskCache.put(taskId, new TaskResult(TaskStatus.COMPLETED, pdf));
            logger.log(Level.INFO, "Generated PDF for task " + taskId);
        } catch (Exception e) {
            taskCache.put(taskId, new TaskResult(TaskStatus.FAILED, null));
            logger.log(Level.WARNING, "Task failed", e);
        }
    }

    private List<Transaction> getAllTransactions(String accountNumber, String fromDate, String toDate) {
        List<Transaction> allTransactions = new ArrayList<>();
        int currentPage = 1;
        boolean lastPage = false;
        while (!lastPage) {
            TransactionResponse response;
            try {
                response = restTemplate.getForObject(
                        "http://localhost:8080/api/v1/transactions?accountNumber={acc}&fromDate={from}&toDate={to}&pageNo={page}",
                        TransactionResponse.class,
                        accountNumber, fromDate, toDate, currentPage);
            } catch (Exception e) {
                response = mockCoreBankingController.getMockTransactions(accountNumber, fromDate, toDate, currentPage);
            }
            if (response != null) {
                allTransactions.addAll(response.transactions());
                lastPage = response.page().lastPage();
                currentPage++;
            }
        }
        return allTransactions;
    }

    private String generatePdf(List<Transaction> transactions) {
        GeneratePdfRequest request = new GeneratePdfRequest(ACCOUNT_STATEMENT, transactions);
        GeneratePdfResponse response = restTemplate.postForObject(
                "http://localhost:8080/api/v1/pdf",
                request,
                GeneratePdfResponse.class);
        return response != null ? response.data() : "";
    }
}