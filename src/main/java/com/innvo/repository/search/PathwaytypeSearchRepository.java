package com.innvo.repository.search;

import com.innvo.domain.Pathwaytype;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Pathwaytype entity.
 */
public interface PathwaytypeSearchRepository extends ElasticsearchRepository<Pathwaytype, Long> {
}
