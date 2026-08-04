# URL Shortener Backend

This repository is being built as a production-style Spring Boot backend for a URL shortener.

## Current foundation

- Spring Boot 3.x
- Java 17 target
- Maven build
- Standard API response envelope
- Global exception handling
- Minimal health endpoint
- Starter security and cache configuration
- Scheduled refresh-token cleanup
- Operations reporting endpoint
- Spring AI-powered growth insights, alias suggestions, and anomaly explanations

## Next planned slices

1. Authentication and user registration
2. URL creation and short-code generation
3. Redirect flow and click analytics
4. Redis caching and rate limiting
5. Scheduler and reporting

## AI features

The AI endpoints use Spring AI with an OpenAI model. They are disabled by default and only activate when `APP_AI_ENABLED=true` and an OpenAI API key is configured in the environment.

Available AI-backed features:

- Generate better aliases
- Generate SEO-friendly aliases
- Generate custom alias suggestions
- Explain analytics
- Generate recommendations
- Answer questions about analytics
- Summarize traffic
- Detect unusual traffic
- Generate marketing insights
