package com.innvo.repository.search;

import com.innvo.domain.Pathwaycategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Pathwaycategory entity.
 */
public interface PathwaycategorySearchRepository extends ElasticsearchRepository<Pathwaycategory, Long> {
}
