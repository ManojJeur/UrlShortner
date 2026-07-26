package com.project.urlshortener.ai;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiAliasService {

    public List<String> suggestAliases(String topic) {
        return List.of(
                topic + "-go",
                topic + "-link",
                topic + "-hub"
        );
    }
}
