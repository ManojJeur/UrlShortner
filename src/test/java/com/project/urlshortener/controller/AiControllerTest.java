package com.project.urlshortener.controller;

import com.project.urlshortener.dto.request.ai.AliasSuggestionRequest;
import com.project.urlshortener.dto.request.ai.AnalyticsQuestionRequest;
import com.project.urlshortener.dto.response.ai.AliasSuggestionResponse;
import com.project.urlshortener.dto.response.ai.AnalyticsQuestionResponse;
import com.project.urlshortener.dto.response.ai.InsightResponse;
import com.project.urlshortener.dto.response.ai.TrafficAnomalyResponse;
import com.project.urlshortener.service.AiInsightsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiController.class)
@AutoConfigureMockMvc(addFilters = false)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiInsightsService aiInsightsService;

    @Test
    void suggestAliasesShouldReturnOk() throws Exception {
        when(aiInsightsService.suggestAliases(any(AliasSuggestionRequest.class))).thenReturn(
                new AliasSuggestionResponse("springtips", "springtips", List.of("springtips"), List.of("spring", "tips"), "Reasoning")
        );

        mockMvc.perform(post("/api/v1/ai/aliases/suggestions")
                        .contentType("application/json")
                        .content("""
                                {
                                  "originalUrl": "https://example.com/articles/spring-boot",
                                  "pageTitle": "Spring Boot Tips",
                                  "brandName": "Example",
                                  "targetKeywords": "spring boot, url shortener",
                                  "tone": "helpful"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.seoFriendlyAlias").value("springtips"));
    }

    @Test
    void analyticsQuestionShouldReturnAnswer() throws Exception {
        when(aiInsightsService.answerQuestion(any(AnalyticsQuestionRequest.class))).thenReturn(
                new AnalyticsQuestionResponse("Why did traffic grow?", "Traffic grew because of the top URL.", List.of("Top URL performed well."), List.of("Publish similar content."))
        );

        mockMvc.perform(post("/api/v1/ai/analytics/questions")
                        .contentType("application/json")
                        .content("""
                                {
                                  "question": "Why did traffic grow?",
                                  "days": 30
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value("Traffic grew because of the top URL."));
    }

    @Test
    void trafficSummaryShouldReturnInsight() throws Exception {
        when(aiInsightsService.summarizeTraffic(anyInt())).thenReturn(
                new InsightResponse("Traffic Summary", "Traffic is healthy.", List.of("Stable growth."), List.of("Keep going."))
        );

        mockMvc.perform(get("/api/v1/ai/traffic/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Traffic Summary"));
    }

    @Test
    void unusualTrafficShouldReturnAnalysis() throws Exception {
        when(aiInsightsService.detectUnusualTraffic(anyInt())).thenReturn(
                new TrafficAnomalyResponse(true, "medium", "Traffic spike", "Spike detected.", List.of("Check campaign."), "14 days")
        );

        mockMvc.perform(get("/api/v1/ai/traffic/unusual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unusual").value(true));
    }
}
