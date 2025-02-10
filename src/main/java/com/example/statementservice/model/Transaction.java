package com.example.statementservice.model;

public record Transaction(
        String trxReferenceNo,
        String valueDate,
        String description,
        String trxType,
        double amount,
        String beneficiaryDetails,
        String tranCurrency
) {}