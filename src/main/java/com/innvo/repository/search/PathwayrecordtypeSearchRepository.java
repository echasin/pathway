package com.innvo.repository.search;

import com.innvo.domain.Pathwayrecordtype;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Pathwayrecordtype entity.
 */
public interface PathwayrecordtypeSearchRepository extends ElasticsearchRepository<Pathwayrecordtype, Long> {
}
