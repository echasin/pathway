package com.innvo.repository.search;

import com.innvo.domain.Pathwaystatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Pathwaystatus entity.
 */
public interface PathwaystatusSearchRepository extends ElasticsearchRepository<Pathwaystatus, Long> {
}
