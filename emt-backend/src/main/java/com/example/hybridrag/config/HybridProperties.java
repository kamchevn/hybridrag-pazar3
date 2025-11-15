package com.example.hybridrag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hybrid")
public record HybridProperties(
		String indexName,
		Integer topK,
		String embedModel,
		String defaultModel
) {}


