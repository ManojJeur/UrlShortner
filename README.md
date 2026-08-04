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
- Spring AI-powered growth insights, alias suggestions, and anomaly explanations through xAI/Grok

## Next planned slices

1. Authentication and user registration
2. URL creation and short-code generation
3. Redirect flow and click analytics
4. Redis caching and rate limiting
5. Scheduler and reporting

## AI features

The AI endpoints use Spring AI with xAI/Grok through xAI's OpenAI-compatible API. They are disabled by default and only activate when `APP_AI_ENABLED=true` and an xAI API key is configured in the environment.

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

## External requirements

You will need these outside the repository for production:

- `XAI_API_KEY`
- `XAI_BASE_URL` if you want to override the default `https://api.x.ai`
- `XAI_MODEL` if you want to pin a specific Grok model
- `APP_AI_ENABLED=true` to turn AI features on
- `APP_BASE_URL` only if you want to override the default public URL
- A production-grade reverse proxy / HTTPS termination
- Regular database backups and restore testing
- Log aggregation and alerting
- A secret-management solution instead of plain environment files

## Render deployment

This repo includes a `render.yaml` blueprint and a Dockerfile so you can deploy the app as a Render web service with Render-managed Postgres and Key Value.

Deployment steps:

1. Push this repository to GitHub.
2. In Render, choose `New` then `Blueprint`, and connect this repo.
3. Render will read `render.yaml` and create:
   - the `urlshortener-api` web service
   - the `urlshortener-db` Postgres database
   - the `urlshortener-cache` Key Value instance
4. When Render prompts you for secrets, provide `XAI_API_KEY` if you want AI features enabled.
5. Deploy the blueprint and wait for the health check at `/actuator/health` to pass.
6. After the service is live, confirm `APP_AI_ENABLED=false` until your xAI key is in place, then set it to `true` to enable Grok-powered features.
7. If you use a custom domain, update `APP_BASE_URL` to that public URL.

Important notes:

- The blueprint wires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `REDIS_URL` from Render-managed infrastructure.
- Render generates `JWT_SECRET` for you on first blueprint creation.
- The app listens on the Render-provided `PORT` automatically.
- The health check uses Spring Boot Actuator, so it will fail if the database or cache connection is broken.
- AI features are intentionally disabled until the `XAI_API_KEY` secret is supplied and `APP_AI_ENABLED` is set to `true`.
