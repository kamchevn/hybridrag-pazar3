package com.example.hybridrag.dto;

import java.util.List;

public record ListingDto(
		String uid,
		String title,
		String url,
		Double priceEur,
		String pricePeriod,
		Double areaM2,
		Integer rooms,
		String location,
		Double lat,
		Double lon,
		List<String> features,
		String agent,
		String phone
) {}


