package com.project.urlshortener.controller;

import com.project.urlshortener.constants.ApiConstants;
import com.project.urlshortener.dto.response.report.OperationsReportResponse;
import com.project.urlshortener.response.ApiResponse;
import com.project.urlshortener.service.ReportingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/reports")
public class ReportController {

    private final ReportingService reportingService;

    public ReportController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/operations")
    public ResponseEntity<ApiResponse<OperationsReportResponse>> operationsReport() {
        OperationsReportResponse response = reportingService.getOperationsReport();
        return ResponseEntity.ok(ApiResponse.success("Operations report fetched successfully", response));
    }
}
