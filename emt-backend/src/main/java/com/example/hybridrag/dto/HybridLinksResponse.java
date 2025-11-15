package com.example.hybridrag.dto;

import java.util.List;

public record HybridLinksResponse(
		List<String> links,
		String usedModel
) {}


