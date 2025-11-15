package com.example.hybridrag.dto;

import java.util.List;

public record HybridAskResponse(
		String answer,
		List<ListingDto> listings,
		String usedModel,
		Integer kVec,
		Integer kListings
) {}


