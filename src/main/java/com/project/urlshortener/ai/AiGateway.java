package com.project.urlshortener.ai;

public interface AiGateway {

    <T> T generateStructured(String systemPrompt, String userPrompt, Class<T> responseType);
}
