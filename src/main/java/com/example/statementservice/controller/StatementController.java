package com.example.statementservice.controller;

import com.example.statementservice.cache.TaskCache;
import com.example.statementservice.model.PDFResponse;
import com.example.statementservice.model.TaskIdResponse;
import com.example.statementservice.model.TaskResult;
import com.example.statementservice.model.TaskStatus;
import com.example.statementservice.service.StatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
public class StatementController {
    @Autowired
    private StatementService statementService;

    @Autowired
    private TaskCache taskCache;

    @Operation(summary = "Generate account statement",
            description = "Initiates asynchronous statement generation and returns task ID")
    @ApiResponse(responseCode = "202", description = "Request accepted for processing")
    @GetMapping("/v1/statements")
    public ResponseEntity<TaskIdResponse> generateStatements(
            @Parameter(description = "Start date in yyyy-dd-MM format") @RequestParam String fromDate,
            @Parameter(description = "End date in yyyy-MM-dd format") @RequestParam String toDate,
            @Parameter(description = "Account number") @RequestParam String accountNumber) {
        String taskId = UUID.randomUUID().toString();
        taskCache.put(taskId, new TaskResult(TaskStatus.PROCESSING, null));
        CompletableFuture.runAsync(() ->
                statementService.generateStatementAsync(taskId, fromDate, toDate, accountNumber));
        return ResponseEntity.accepted().body(new TaskIdResponse(taskId));
    }

    @Operation(summary = "Get generated statement",
            description = "Retrieves generated PDF statement by task ID")
    @ApiResponse(responseCode = "200", description = "PDF statement available")
    @ApiResponse(responseCode = "202", description = "Processing still in progress", content = @Content)
    @ApiResponse(responseCode = "404", description = "Task ID not found", content = @Content)
    @ApiResponse(responseCode = "502", description = "Task failed", content = @Content)
    @GetMapping("/v1/statements/{taskId}")
    public ResponseEntity<PDFResponse> getStatementFromTask(
            @Parameter(description = "Task ID received from /v1/statements") @PathVariable String taskId) {
        TaskResult result = taskCache.get(taskId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return switch (result.status()) {
            case TaskStatus.PROCESSING -> ResponseEntity.accepted().build();
            case TaskStatus.COMPLETED -> {
                taskCache.delete(taskId);
                yield ResponseEntity.ok().body(new PDFResponse(result.pdfBase64()));
            }
            case TaskStatus.FAILED -> {
                taskCache.delete(taskId);
                yield ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
            }
        };
    }
}