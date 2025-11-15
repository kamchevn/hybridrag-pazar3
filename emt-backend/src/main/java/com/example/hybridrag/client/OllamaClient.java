package com.example.hybridrag.client;

import com.example.hybridrag.config.HybridProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class OllamaClient {
	private final WebClient client;
	private final HybridProperties props;
	private final ObjectMapper mapper = new ObjectMapper();
	private final Duration timeout;

	public OllamaClient(WebClient ollamaWebClient, HybridProperties props,
	                    @org.springframework.beans.factory.annotation.Value("${ollama.timeout-seconds:300}") long timeoutSeconds) {
		this.client = ollamaWebClient;
		this.props = props;
		this.timeout = Duration.ofSeconds(timeoutSeconds);
	}

	public List<Double> embedText(String text) {
		try {
			String body = mapper.createObjectNode()
					.put("model", props.embedModel())
					.put("prompt", text)
					.toString();
			String response = client.post()
					.uri("/api/embeddings")
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(body)
					.retrieve()
					.bodyToMono(String.class)
					.timeout(this.timeout)
					.block();
			JsonNode root = mapper.readTree(Objects.requireNonNull(response, "null embeddings response"));
			JsonNode arr = root.get("embedding");
			List<Double> result = new ArrayList<>();
			if (arr != null && arr.isArray()) {
				for (JsonNode n : arr) result.add(n.asDouble());
				return result;
			}
			throw new IllegalStateException("Unexpected embeddings response: " + response);
		} catch (Exception e) {
			throw new RuntimeException("Ollama embeddings failed", e);
		}
	}

	public String generateText(String model, String prompt, int numPredict) {
		try {
			String payload = mapper.createObjectNode()
					.put("model", model)
					.put("prompt", prompt)
					.put("stream", false)
					.set("options", mapper.createObjectNode().put("num_predict", numPredict))
					.toString();
			String response = client.post()
					.uri("/api/generate")
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(payload)
					.retrieve()
					.bodyToMono(String.class)
					.timeout(this.timeout)
					.block();
			JsonNode root = mapper.readTree(Objects.requireNonNull(response, "null generate response"));
			JsonNode resp = root.get("response");
			return resp != null && resp.isTextual() ? resp.asText() : response;
		} catch (Exception e) {
			throw new RuntimeException("Ollama generate failed", e);
		}
	}
}


