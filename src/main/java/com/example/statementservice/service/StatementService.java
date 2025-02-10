package com.example.statementservice.service;

import com.example.statementservice.cache.TaskCache;
import com.example.statementservice.model.TaskResult;
import com.example.statementservice.model.TaskStatus;
import com.example.statementservice.model.Transaction;
import com.example.statementservice.rest.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class StatementService {
    private final Logger logger = Logger.getLogger(StatementService.class.getName());

    @Autowired
    private RestClient restClient;

    @Autowired
    private TaskCache taskCache;

    public void generateStatements(String taskId, String fromDate, String toDate, String accountNumber) {
        try {
            List<Transaction> transactions = restClient.getAllTransactions(accountNumber, fromDate, toDate);
            String pdf = restClient.generatePdf(transactions);
            taskCache.put(taskId, new TaskResult(TaskStatus.COMPLETED, pdf));
            logger.log(Level.INFO, "Generated PDF for task " + taskId);
        } catch (Exception e) {
            taskCache.put(taskId, new TaskResult(TaskStatus.FAILED, null));
            logger.log(Level.WARNING, "Task failed", e);
        }
    }
}