package com.project.urlshortener.service;

import com.project.urlshortener.dto.response.report.OperationsReportResponse;

public interface ReportingService {

    OperationsReportResponse getOperationsReport();
}
