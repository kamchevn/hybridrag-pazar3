package com.example.hybridrag.service;

import com.example.hybridrag.client.OllamaClient;
import com.example.hybridrag.config.HybridProperties;
import com.example.hybridrag.dto.HybridAskResponse;
import com.example.hybridrag.dto.HybridLinksResponse;
import com.example.hybridrag.dto.ListingDto;
import com.example.hybridrag.repository.HybridRagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HybridRagService {
	private final HybridRagRepository repo;
	private final OllamaClient ollama;
	private final HybridProperties props;

	public HybridRagService(HybridRagRepository repo, OllamaClient ollama, HybridProperties props) {
		this.repo = repo;
		this.ollama = ollama;
		this.props = props;
	}

	@Transactional(readOnly = true)
	public HybridAskResponse ask(String question, String modelOverride, Integer kVec, Integer kListings) {
		int k1 = kVec != null ? kVec : 12;
		int k2 = kListings != null ? kListings : 8;
		String model = StringUtils.hasText(modelOverride) ? modelOverride : props.defaultModel();

		List<Double> embedding = ollama.embedText(question);
		List<String> chunkIds = repo.vectorChunkIds(props.indexName(), embedding, k1);
		List<ListingDto> listings = repo.expandListings(chunkIds, k2);
		String prompt = buildAskPrompt(question, listings, chunkIds);
		String answer = ollama.generateText(model, prompt, 220);
		return new HybridAskResponse(answer, listings, model, k1, k2);
	}

	@Transactional(readOnly = true)
	public HybridLinksResponse links(String question, String modelOverride, Integer kVec, Integer kListings) {
		int k1 = kVec != null ? kVec : 12;
		int k2 = kListings != null ? kListings : 8;
		String model = StringUtils.hasText(modelOverride) ? modelOverride : props.defaultModel();

		List<Double> embedding = ollama.embedText(question);
		List<String> chunkIds = repo.vectorChunkIds(props.indexName(), embedding, k1);
		List<ListingDto> listings = repo.expandListings(chunkIds, k2);

		// Option: return just candidate URLs without LLM filtering
		List<String> urls = listings.stream()
				.map(ListingDto::url)
				.filter(StringUtils::hasText)
				.distinct()
				.collect(Collectors.toList());
		if (!urls.isEmpty()) {
			return new HybridLinksResponse(urls, null);
		}

		// Fallback: derive citations from chunks
		List<String> citations = repo.citationLinksForChunks(chunkIds, Math.min(8, chunkIds.size()));
		List<String> clean = citations.stream()
				.filter(s -> s != null && s.startsWith("http"))
				.distinct()
				.collect(Collectors.toList());
		return new HybridLinksResponse(clean, null);
	}

	private String buildAskPrompt(String question, List<ListingDto> listings, List<String> chunkIds) {
		StringBuilder table = new StringBuilder();
		for (ListingDto l : listings) {
			table.append("- ");
			table.append(z(l.title())).append(" | ");
			table.append("price: ").append(l.priceEur() != null ? l.priceEur().intValue() + " EUR" : "?")
					.append(" ").append(z(l.pricePeriod())).append(" | ");
			table.append("area: ").append(l.areaM2() != null ? l.areaM2().intValue() + " m2" : "?").append(" | ");
			table.append("rooms: ").append(l.rooms() != null ? l.rooms() : "?").append(" | ");
			table.append("location: ").append(z(l.location())).append(" | ");
			table.append("features: ").append(l.features() != null ? String.join(", ", l.features()) : "?").append(" | ");
			table.append("url: ").append(z(l.url())).append("\n");
		}
		List<String> citations = new ArrayList<>();
		List<String> links = repo.citationLinksForChunks(chunkIds, Math.min(8, chunkIds.size()));
		for (int i = 0; i < links.size(); i++) {
			String link = links.get(i);
			if (link == null) link = "(no link)";
			citations.add("[" + (i + 1) + "] " + link);
		}
		String prompt = """
				Одговори на македонски, концизно но детално, ИСКЛУЧИВО од табелата и цитатите.

				ПРАШАЊЕ:
				%s

				ТАБЕЛА (огласи):
				%s

				ЦИТАТИ (линкови кон огласите):
				%s

				Правила:
				- Прво објасни накратко што најде (колку огласи се релевантни).
				- Потоа наведи 2–5 најрелевантни опции со цена, локација, тип (стан/куќа), издавање/продажба.
				- Ако има features (Балкон / Тераса, Паркинг, Нова градба итн.), искористи ги.
				- На крај предложи кои огласи најмногу одговараат на барањето и зошто.
				""";
		return String.format(prompt, question, table.toString(), String.join("\n", citations));
	}

	private String z(String s) { return s == null ? "?" : s; }
}


