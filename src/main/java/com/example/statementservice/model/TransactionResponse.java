package com.example.statementservice.model;

import java.util.List;

public record TransactionResponse(
        List<Transaction> transactions,
        PageInfo page
) {}