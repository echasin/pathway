package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Pathwaycategory;

import com.innvo.repository.PathwaycategoryRepository;
import com.innvo.repository.search.PathwaycategorySearchRepository;
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
 * REST controller for managing Pathwaycategory.
 */
@RestController
@RequestMapping("/api")
public class PathwaycategoryResource {

    private final Logger log = LoggerFactory.getLogger(PathwaycategoryResource.class);

    private static final String ENTITY_NAME = "pathwaycategory";

    private final PathwaycategoryRepository pathwaycategoryRepository;

    private final PathwaycategorySearchRepository pathwaycategorySearchRepository;

    public PathwaycategoryResource(PathwaycategoryRepository pathwaycategoryRepository, PathwaycategorySearchRepository pathwaycategorySearchRepository) {
        this.pathwaycategoryRepository = pathwaycategoryRepository;
        this.pathwaycategorySearchRepository = pathwaycategorySearchRepository;
    }

    /**
     * POST  /pathwaycategories : Create a new pathwaycategory.
     *
     * @param pathwaycategory the pathwaycategory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pathwaycategory, or with status 400 (Bad Request) if the pathwaycategory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/pathwaycategories")
    @Timed
    public ResponseEntity<Pathwaycategory> createPathwaycategory(@Valid @RequestBody Pathwaycategory pathwaycategory) throws URISyntaxException {
        log.debug("REST request to save Pathwaycategory : {}", pathwaycategory);
        if (pathwaycategory.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new pathwaycategory cannot already have an ID")).body(null);
        }
        Pathwaycategory result = pathwaycategoryRepository.save(pathwaycategory);
        pathwaycategorySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pathwaycategories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pathwaycategories : Updates an existing pathwaycategory.
     *
     * @param pathwaycategory the pathwaycategory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pathwaycategory,
     * or with status 400 (Bad Request) if the pathwaycategory is not valid,
     * or with status 500 (Internal Server Error) if the pathwaycategory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/pathwaycategories")
    @Timed
    public ResponseEntity<Pathwaycategory> updatePathwaycategory(@Valid @RequestBody Pathwaycategory pathwaycategory) throws URISyntaxException {
        log.debug("REST request to update Pathwaycategory : {}", pathwaycategory);
        if (pathwaycategory.getId() == null) {
            return createPathwaycategory(pathwaycategory);
        }
        Pathwaycategory result = pathwaycategoryRepository.save(pathwaycategory);
        pathwaycategorySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, pathwaycategory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pathwaycategories : get all the pathwaycategories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pathwaycategories in body
     */
    @GetMapping("/pathwaycategories")
    @Timed
    public ResponseEntity<List<Pathwaycategory>> getAllPathwaycategories(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Pathwaycategories");
        Page<Pathwaycategory> page = pathwaycategoryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pathwaycategories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pathwaycategories/:id : get the "id" pathwaycategory.
     *
     * @param id the id of the pathwaycategory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pathwaycategory, or with status 404 (Not Found)
     */
    @GetMapping("/pathwaycategories/{id}")
    @Timed
    public ResponseEntity<Pathwaycategory> getPathwaycategory(@PathVariable Long id) {
        log.debug("REST request to get Pathwaycategory : {}", id);
        Pathwaycategory pathwaycategory = pathwaycategoryRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(pathwaycategory));
    }

    /**
     * DELETE  /pathwaycategories/:id : delete the "id" pathwaycategory.
     *
     * @param id the id of the pathwaycategory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/pathwaycategories/{id}")
    @Timed
    public ResponseEntity<Void> deletePathwaycategory(@PathVariable Long id) {
        log.debug("REST request to delete Pathwaycategory : {}", id);
        pathwaycategoryRepository.delete(id);
        pathwaycategorySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/pathwaycategories?query=:query : search for the pathwaycategory corresponding
     * to the query.
     *
     * @param query the query of the pathwaycategory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/pathwaycategories")
    @Timed
    public ResponseEntity<List<Pathwaycategory>> searchPathwaycategories(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Pathwaycategories for query {}", query);
        Page<Pathwaycategory> page = pathwaycategorySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pathwaycategories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
