package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Vector;

import com.innvo.repository.VectorRepository;
import com.innvo.repository.search.VectorSearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
import com.innvo.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Vector.
 */
@RestController
@RequestMapping("/api")
public class VectorResource {

    private final Logger log = LoggerFactory.getLogger(VectorResource.class);

    private static final String ENTITY_NAME = "vector";

    private final VectorRepository vectorRepository;

    private final VectorSearchRepository vectorSearchRepository;

    public VectorResource(VectorRepository vectorRepository, VectorSearchRepository vectorSearchRepository) {
        this.vectorRepository = vectorRepository;
        this.vectorSearchRepository = vectorSearchRepository;
    }

    /**
     * POST  /vectors : Create a new vector.
     *
     * @param vector the vector to create
     * @return the ResponseEntity with status 201 (Created) and with body the new vector, or with status 400 (Bad Request) if the vector has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/vectors")
    @Timed
    public ResponseEntity<Vector> createVector(@Valid @RequestBody Vector vector) throws URISyntaxException {
        log.debug("REST request to save Vector : {}", vector);
        if (vector.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new vector cannot already have an ID")).body(null);
        }
        Vector result = vectorRepository.save(vector);
        vectorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/vectors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /vectors : Updates an existing vector.
     *
     * @param vector the vector to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated vector,
     * or with status 400 (Bad Request) if the vector is not valid,
     * or with status 500 (Internal Server Error) if the vector couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/vectors")
    @Timed
    public ResponseEntity<Vector> updateVector(@Valid @RequestBody Vector vector) throws URISyntaxException {
        log.debug("REST request to update Vector : {}", vector);
        if (vector.getId() == null) {
            return createVector(vector);
        }
        Vector result = vectorRepository.save(vector);
        vectorSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, vector.getId().toString()))
            .body(result);
    }

    /**
     * GET  /vectors : get all the vectors.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of vectors in body
     */
    @GetMapping("/vectors")
    @Timed
    public ResponseEntity<List<Vector>> getAllVectors(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Vectors");
        Page<Vector> page = vectorRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/vectors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /vectors/:id : get the "id" vector.
     *
     * @param id the id of the vector to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the vector, or with status 404 (Not Found)
     */
    @GetMapping("/vectors/{id}")
    @Timed
    public ResponseEntity<Vector> getVector(@PathVariable Long id) {
        log.debug("REST request to get Vector : {}", id);
        Vector vector = vectorRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(vector));
    }

    /**
     * DELETE  /vectors/:id : delete the "id" vector.
     *
     * @param id the id of the vector to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/vectors/{id}")
    @Timed
    public ResponseEntity<Void> deleteVector(@PathVariable Long id) {
        log.debug("REST request to delete Vector : {}", id);
        vectorRepository.delete(id);
        vectorSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/vectors?query=:query : search for the vector corresponding
     * to the query.
     *
     * @param query the query of the vector search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/vectors")
    @Timed
    public ResponseEntity<List<Vector>> searchVectors(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Vectors for query {}", query);
        Page<Vector> page = vectorSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/vectors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
