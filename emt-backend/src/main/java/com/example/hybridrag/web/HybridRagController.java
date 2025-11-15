package com.example.hybridrag.web;

import com.example.hybridrag.dto.HybridAskResponse;
import com.example.hybridrag.dto.HybridLinksResponse;
import com.example.hybridrag.service.HybridRagService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/hybrid", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class HybridRagController {
	private final HybridRagService service;

	public HybridRagController(HybridRagService service) {
		this.service = service;
	}

	@GetMapping("/ask")
	public HybridAskResponse ask(@RequestParam("q") String q,
	                             @RequestParam(value = "model", required = false) String model,
	                             @RequestParam(value = "kVec", required = false) Integer kVec,
	                             @RequestParam(value = "kListings", required = false) Integer kListings) {
		return service.ask(q, model, kVec, kListings);
	}

	@GetMapping("/links")
	public HybridLinksResponse links(@RequestParam("q") String q,
	                                 @RequestParam(value = "model", required = false) String model,
	                                 @RequestParam(value = "kVec", required = false) Integer kVec,
	                                 @RequestParam(value = "kListings", required = false) Integer kListings) {
		return service.links(q, model, kVec, kListings);
	}
}


