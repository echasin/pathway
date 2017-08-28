package com.innvo.repository.search;

import com.innvo.domain.Vector;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Vector entity.
 */
public interface VectorSearchRepository extends ElasticsearchRepository<Vector, Long> {
}
