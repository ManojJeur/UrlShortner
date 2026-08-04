package com.project.urlshortener.controller;

import com.project.urlshortener.dto.response.ShortUrlResponse;
import com.project.urlshortener.dto.response.PageResponse;
import com.project.urlshortener.dto.request.UpdateShortUrlRequest;
import com.project.urlshortener.dto.request.UpdateShortUrlStatusRequest;
import com.project.urlshortener.dto.request.ShortUrlSearchRequest;
import com.project.urlshortener.service.AnalyticsService;
import com.project.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@AutoConfigureMockMvc(addFilters = false)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    void createShortUrlShouldReturnCreated() throws Exception {
        when(urlService.createShortUrl(any(), any())).thenReturn(sampleResponse());
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", null, Collections.emptyList());

        mockMvc.perform(post("/api/v1/urls")
                        .with(authentication(authentication))
                        .contentType("application/json")
                        .content("""
                                {"originalUrl":"https://example.com/articles/spring-boot","customAlias":"springtips","expiresAt":"2026-08-01T00:00:00Z"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.shortCode").value("springtips"));
    }

    @Test
    void redirectShouldReturnFound() throws Exception {
        when(urlService.resolveOriginalUrl("springtips")).thenReturn("https://example.com/articles/spring-boot");

        mockMvc.perform(get("/api/v1/r/springtips"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/articles/spring-boot"));

        verify(analyticsService).recordClick(eq("springtips"), any());
    }

    @Test
    void listMyShortUrlsShouldReturnOk() throws Exception {
        when(urlService.listMyShortUrls(anyString(), any(ShortUrlSearchRequest.class))).thenReturn(new PageResponse<>(
                List.of(sampleResponse()),
                0,
                20,
                1L,
                1,
                false,
                false
        ));
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", null, Collections.emptyList());

        mockMvc.perform(get("/api/v1/urls")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(urlService).listMyShortUrls(eq("user@example.com"), any(ShortUrlSearchRequest.class));
    }

    @Test
    void updateShortUrlShouldReturnOk() throws Exception {
        when(urlService.updateShortUrl(eq("user@example.com"), eq(1L), any(UpdateShortUrlRequest.class))).thenReturn(sampleResponse());
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", null, Collections.emptyList());

        mockMvc.perform(patch("/api/v1/urls/1")
                        .with(authentication(authentication))
                        .contentType("application/json")
                        .content("""
                                {"originalUrl":"https://example.com/new","customAlias":"springtips","expiresAt":"2026-08-01T00:00:00Z"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateShortUrlStatusShouldReturnOk() throws Exception {
        when(urlService.updateShortUrlStatus(eq("user@example.com"), eq(1L), any(UpdateShortUrlStatusRequest.class))).thenReturn(sampleResponse());
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", null, Collections.emptyList());

        mockMvc.perform(patch("/api/v1/urls/1/status")
                        .with(authentication(authentication))
                        .contentType("application/json")
                        .content("""
                                {"enabled":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteShortUrlShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/urls/1")
                        .with(authentication(new UsernamePasswordAuthenticationToken("user@example.com", null, Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(urlService).deleteShortUrl(eq("user@example.com"), eq(1L));
    }

    private ShortUrlResponse sampleResponse() {
        return new ShortUrlResponse(1L, "https://example.com/articles/spring-boot", "springtips", "http://localhost:8080/api/v1/r/springtips", "springtips", 0L, true, Instant.parse("2026-08-01T00:00:00Z"), Instant.now());
    }
}
