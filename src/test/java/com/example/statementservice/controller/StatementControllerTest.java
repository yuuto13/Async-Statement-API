package com.example.statementservice.controller;

import com.example.statementservice.cache.TaskCache;
import com.example.statementservice.model.TaskResult;
import com.example.statementservice.model.TaskStatus;
import com.example.statementservice.service.StatementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatementController.class)
//@AutoConfigureMockMvc(addFilters = false)
//@ExtendWith(MockitoExtension.class)
class StatementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatementService statementService;

    @MockitoBean
    private TaskCache taskCache;

    @Test
    void generateStatements_shouldReturnAcceptedWithTaskId() throws Exception {
        mockMvc.perform(get("/v1/statements")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-06-30")
                        .param("accountNumber", "1000000001"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.taskId").exists());
    }

    @Test
    void getStatementFromTask_shouldReturnProcessingWhenStatusProcessing() throws Exception {
        when(taskCache.get("9d6ca36d-d2a8-4541-a66a-55cf37a102a9")).thenReturn(new TaskResult(TaskStatus.PROCESSING, null));
        mockMvc.perform(get("/v1/statements/9d6ca36d-d2a8-4541-a66a-55cf37a102a9"))
                .andExpect(status().isAccepted());
    }

    @Test
    void getStatementFromTask_shouldReturnPdfAndClearCacheWhenCompleted() throws Exception {
        String mockPdf = "JVBERi0xLjUKJYCBgoMKMSAwIG9iago8PC9GaWx0ZXIvRmxhdGVEZWNvZGUvRmlyc3QgMTQxL04gMjAvTGVuZ3==";
        when(taskCache.get("9d6ca36d-d2a8-4541-a66a-55cf37a102a9")).thenReturn(new TaskResult(TaskStatus.COMPLETED, mockPdf));
        mockMvc.perform(get("/v1/statements/9d6ca36d-d2a8-4541-a66a-55cf37a102a9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pdf").value(mockPdf));
        verify(taskCache).delete("9d6ca36d-d2a8-4541-a66a-55cf37a102a9");
    }

    @Test
    void getStatementFromTask_shouldReturnNotFoundForInvalidTaskId() throws Exception {
        when(taskCache.get("invalid")).thenReturn(null);
        mockMvc.perform(get("/v1/statements/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStatementFromTask_shouldReturn502WhenTaskFailed() throws Exception {
        when(taskCache.get("failedTask")).thenReturn(new TaskResult(TaskStatus.FAILED, null));
        mockMvc.perform(get("/v1/statements/failedTask"))
                .andExpect(status().isBadGateway());
        verify(taskCache).delete("failedTask");
    }
}