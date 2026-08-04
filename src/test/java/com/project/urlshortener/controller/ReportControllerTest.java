package com.project.urlshortener.controller;

import com.project.urlshortener.dto.response.report.OperationsReportResponse;
import com.project.urlshortener.service.ReportingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportingService reportingService;

    @Test
    void operationsReportShouldReturnSummary() throws Exception {
        when(reportingService.getOperationsReport()).thenReturn(new OperationsReportResponse(
                Instant.parse("2026-08-02T00:00:00Z"),
                3L,
                8L,
                5L,
                2L,
                42L,
                7L,
                4L,
                2L,
                1L
        ));

        mockMvc.perform(get("/api/v1/reports/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsers").value(3))
                .andExpect(jsonPath("$.data.totalClickEvents").value(42));
    }
}
