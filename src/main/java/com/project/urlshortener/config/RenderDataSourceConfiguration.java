package com.project.urlshortener.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Render provides Postgres as a standard PostgreSQL URI with credentials in
 * the authority section. pgJDBC expects a JDBC URL with credentials supplied
 * separately, so translate the URI for the production Render profile.
 */
@Configuration
@Profile("render")
public class RenderDataSourceConfiguration {

    @Bean
    public DataSource dataSource(Environment environment) {
        String renderDatabaseUrl = environment.getRequiredProperty("DB_URL");
        String username = environment.getRequiredProperty("DB_USERNAME");
        String password = environment.getRequiredProperty("DB_PASSWORD");

        URI databaseUri;
        try {
            databaseUri = URI.create(renderDatabaseUrl);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("DB_URL must be a valid PostgreSQL connection URL");
        }

        String scheme = databaseUri.getScheme();
        if (!("postgresql".equalsIgnoreCase(scheme) || "postgres".equalsIgnoreCase(scheme))
                || !StringUtils.hasText(databaseUri.getHost())
                || !StringUtils.hasText(databaseUri.getRawPath())
                || databaseUri.getRawPath().length() < 2) {
            throw new IllegalStateException("DB_URL must use the postgresql://user:password@host/database format");
        }

        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                .append(databaseUri.getHost());
        if (databaseUri.getPort() != -1) {
            jdbcUrl.append(':').append(databaseUri.getPort());
        }
        jdbcUrl.append(databaseUri.getRawPath());
        if (StringUtils.hasText(databaseUri.getRawQuery())) {
            jdbcUrl.append('?').append(databaseUri.getRawQuery());
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl.toString());
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}
