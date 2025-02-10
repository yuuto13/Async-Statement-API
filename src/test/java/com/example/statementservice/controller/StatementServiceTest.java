package com.example.statementservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGenerateTaskIdAndReturnsAcceptedStatus() throws Exception {
        mockMvc.perform(get("/v1/statements")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-06-30")
                        .param("accountNumber", "1000000001"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.taskId").exists());
    }
}