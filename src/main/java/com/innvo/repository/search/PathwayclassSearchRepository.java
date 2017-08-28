package com.innvo.repository.search;

import com.innvo.domain.Pathwayclass;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Pathwayclass entity.
 */
public interface PathwayclassSearchRepository extends ElasticsearchRepository<Pathwayclass, Long> {
}
