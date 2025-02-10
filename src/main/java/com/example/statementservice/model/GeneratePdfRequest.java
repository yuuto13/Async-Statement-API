package com.example.statementservice.model;

import java.util.List;

public record GeneratePdfRequest(
        String templateId,
        List<Transaction> data
) {}