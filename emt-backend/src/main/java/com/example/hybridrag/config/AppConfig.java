package com.example.hybridrag.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties({OllamaProperties.class, HybridProperties.class})
public class AppConfig {

	@Bean
	public WebClient ollamaWebClient(OllamaProperties props) {
		int size = 32 * 1024 * 1024;
		return WebClient.builder()
				.baseUrl(props.baseUrl())
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(c -> c.defaultCodecs().maxInMemorySize(size))
						.build())
				.build();
	}
}


