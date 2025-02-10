package com.example.statementservice.controller;

import com.example.statementservice.model.GeneratePdfRequest;
import com.example.statementservice.model.GeneratePdfResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockTemplateEngineController {
    @Hidden
    @PostMapping("/api/v1/pdf")
    public GeneratePdfResponse generateMockPdf(@RequestBody GeneratePdfRequest request) {
        return new GeneratePdfResponse(
                "JVBERi0xLjUKJYCBgoMKMSAwIG9iago8PC9GaWx0ZXIvRmxhdGVEZWNvZGUvRmlyc3QgMTQxL04gMjAvTGVuZ3==");
    }
}