package com.project.urlshortener;

import com.project.urlshortener.config.AppProperties;
import com.project.urlshortener.config.AiProperties;
import com.project.urlshortener.config.JwtProperties;
import com.project.urlshortener.config.RedisAppProperties;
import com.project.urlshortener.config.ShortUrlProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties({AppProperties.class, AiProperties.class, JwtProperties.class, ShortUrlProperties.class, RedisAppProperties.class})
public class UrlShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerApplication.class, args);
    }
}
