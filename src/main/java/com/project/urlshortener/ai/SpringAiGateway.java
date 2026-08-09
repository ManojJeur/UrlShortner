package com.project.urlshortener.ai;

import com.project.urlshortener.constants.ErrorCodes;
import com.project.urlshortener.exception.ServiceUnavailableException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.ai", name = "enabled", havingValue = "true")
public class SpringAiGateway implements AiGateway {

    private final ChatClient chatClient;

    public SpringAiGateway(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public <T> T generateStructured(String systemPrompt, String userPrompt, Class<T> responseType) {
        try {
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .entity(responseType);
        } catch (RuntimeException exception) {
            throw new ServiceUnavailableException(
                    "AI service is temporarily unavailable",
                    ErrorCodes.SERVICE_UNAVAILABLE
            );
        }
    }
}
