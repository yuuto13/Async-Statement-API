package com.example.statementservice.controller;

import com.example.statementservice.model.Transaction;
import com.example.statementservice.model.TransactionResponse;
import com.example.statementservice.model.PageInfo;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class MockCoreBankingController {
    @Hidden
    @GetMapping("/api/v1/transactions")
    public TransactionResponse getMockTransactions(
            @RequestParam String accountNumber,
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam int pageNo) {
        // Simulate delay
        try {
            int secondsToSleep = 10;
            Thread.sleep(secondsToSleep * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        return switch (pageNo) {
            case 1 -> new TransactionResponse(
                    List.of(
                            new Transaction("010000032", "2024-11-12T00:00:00",
                                    "Fund transfer", "D", 100, "Friends Name", "AED"),
                            new Transaction("010000033", "2024-11-12T00:00:00",
                                    "Bill Payment", "D", 500, "Friends Name", "AED")
                    ),
                    new PageInfo(false, 1));
            case 2 -> new TransactionResponse(
                    List.of(
                            new Transaction("010000034", "2024-11-12T00:00:00",
                                    "Fund transfer", "D", 100, "Friends Name", "AED"),
                            new Transaction("010000035", "2024-11-12T00:00:00",
                                    "Bill Payment", "D", 500, "Friends Name", "AED")
                    ),
                    new PageInfo(true, 2));
            default -> new TransactionResponse(List.of(), new PageInfo(true, pageNo));
        };
    }
}