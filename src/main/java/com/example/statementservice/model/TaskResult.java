package com.example.statementservice.model;

public record TaskResult(
        TaskStatus status,
        String pdfBase64
) {}