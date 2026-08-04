package com.project.urlshortener.service;

import com.project.urlshortener.dto.request.ai.AliasSuggestionRequest;
import com.project.urlshortener.dto.request.ai.AnalyticsQuestionRequest;
import com.project.urlshortener.dto.response.ai.AliasSuggestionResponse;
import com.project.urlshortener.dto.response.ai.AnalyticsQuestionResponse;
import com.project.urlshortener.dto.response.ai.InsightResponse;
import com.project.urlshortener.dto.response.ai.TrafficAnomalyResponse;

public interface AiInsightsService {

    AliasSuggestionResponse suggestAliases(AliasSuggestionRequest request);

    InsightResponse summarizeTraffic(int days);

    InsightResponse explainAnalytics(int days);

    InsightResponse generateRecommendations(int days);

    AnalyticsQuestionResponse answerQuestion(AnalyticsQuestionRequest request);

    TrafficAnomalyResponse detectUnusualTraffic(int days);

    InsightResponse generateMarketingInsights(int days);
}
