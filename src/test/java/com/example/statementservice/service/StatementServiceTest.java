package com.example.statementservice.service;

import com.example.statementservice.cache.TaskCache;
import com.example.statementservice.model.TaskResult;
import com.example.statementservice.model.TaskStatus;
import com.example.statementservice.model.Transaction;
import com.example.statementservice.rest.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {
    @Mock
    private RestClient restClient;

    @Mock
    private TaskCache taskCache;

    @InjectMocks
    private StatementService statementService;

    @Test
    public void GenerateStatements_ReturnVoid() {
        List<Transaction> transactions = List.of(
                new Transaction("010000032", "2024-11-12T00:00:00",
                        "Fund transfer", "D", 100, "Friends Name", "AED"),
                new Transaction("010000033", "2024-11-12T00:00:00",
                        "Bill Payment", "D", 500, "Friends Name", "AED")
        );
        String pdf = "JVBERi0xLjUKJYCBgoMKMSAwIG9iago8PC9GaWx0ZXIvRmxhdGVEZWNvZGUvRmlyc3QgMTQxL04gMjAvTGVuZ3==";
        String taskId = "9d6ca36d-d2a8-4541-a66a-55cf37a102a9";
        when(restClient.getAllTransactions("1000000001", "2024-01-01", "2024-06-30")).thenReturn(transactions);
        when(restClient.generatePdf(transactions)).thenReturn(pdf);
        doNothing().when(taskCache).put(taskId, new TaskResult(TaskStatus.COMPLETED, pdf));
        assertAll(() -> statementService.generateStatements(taskId, "2024-01-01", "2024-06-30", "1000000001"));
    }
}
