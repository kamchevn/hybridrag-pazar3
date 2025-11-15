package com.example.hybridrag.repository;

import com.example.hybridrag.dto.ListingDto;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class HybridRagRepository {
	private final Neo4jClient neo4j;

	public HybridRagRepository(Neo4jClient neo4j) {
		this.neo4j = neo4j;
	}

	public List<String> vectorChunkIds(String indexName, List<Double> embedding, int k) {
		String cypher = """
				CALL db.index.vector.queryNodes($index, $k, $embedding)
				YIELD node, score
				RETURN node.chunkId AS chunkId
				""";
		return neo4j.query(cypher)
				.bindAll(Map.of("index", indexName, "k", k, "embedding", embedding))
				.fetch().all().stream()
				.map(m -> (String) m.get("chunkId"))
				.collect(Collectors.toList());
	}

	public List<ListingDto> expandListings(List<String> chunkIds, int k) {
		if (chunkIds == null || chunkIds.isEmpty()) return List.of();
		String cypher = """
				UNWIND $ids AS cid
				MATCH (l:Listing)-[:MENTIONED_IN]->(c:ChunkP3 {chunkId: cid})
				OPTIONAL MATCH (l)-[:LOCATED_IN]->(loc:Location)
				OPTIONAL MATCH (l)-[:HAS_FEATURE]->(f:Feature)
				OPTIONAL MATCH (l)-[:LISTED_BY]->(a:Agent)
				WITH l, loc, collect(DISTINCT f.name) AS feats, a
				RETURN l.uid AS uid, l.title AS title, l.price_eur AS eur,
				       l.price_period AS period, l.area_m2 AS m2, l.rooms AS rooms,
				       coalesce(loc.name,'?') AS location, loc.lat AS lat, loc.lon AS lon,
				       feats AS features, coalesce(a.name,'?') AS agent, a.phone AS phone,
				       l.url AS url
				LIMIT $k
				""";
		java.util.Collection<Map<String, Object>> rows = neo4j.query(cypher)
				.bindAll(Map.of("ids", chunkIds, "k", k))
				.fetch().all();
		List<ListingDto> result = new ArrayList<>();
		for (Map<String, Object> row : rows) {
			@SuppressWarnings("unchecked")
			List<String> feats = (List<String>) row.getOrDefault("features", List.of());
			result.add(new ListingDto(
					(String) row.get("uid"),
					(String) row.get("title"),
					(String) row.get("url"),
					toDouble(row.get("eur")),
					(String) row.get("period"),
					toDouble(row.get("m2")),
					toInteger(row.get("rooms")),
					(String) row.get("location"),
					toDouble(row.get("lat")),
					toDouble(row.get("lon")),
					feats,
					(String) row.get("agent"),
					(String) row.get("phone")
			));
		}
		return result;
	}

	public List<String> citationLinksForChunks(List<String> chunkIds, int maxCount) {
		if (chunkIds == null || chunkIds.isEmpty()) return List.of();
		String cypher = """
				UNWIND $ids AS cid
				MATCH (c:ChunkP3 {chunkId: cid})
				OPTIONAL MATCH (l:Listing)-[:MENTIONED_IN]->(c)
				RETURN coalesce(l.url, c.url) AS link
				LIMIT $k
				""";
		return neo4j.query(cypher)
				.bindAll(Map.of("ids", chunkIds, "k", maxCount))
				.fetch().all().stream()
				.map(m -> (String) m.get("link"))
				.map(s -> s == null ? "(no link)" : s)
				.collect(Collectors.toList());
	}

	private Double toDouble(Object v) {
		if (v == null) return null;
		if (v instanceof Number n) return n.doubleValue();
		try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
	}

	private Integer toInteger(Object v) {
		if (v == null) return null;
		if (v instanceof Number n) return n.intValue();
		try { return Integer.parseInt(v.toString()); } catch (Exception e) { return null; }
	}
}


