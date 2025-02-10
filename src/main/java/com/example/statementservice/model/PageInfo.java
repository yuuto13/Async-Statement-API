package com.example.statementservice.model;

public record PageInfo(
        boolean lastPage,
        int currentPage
) {}