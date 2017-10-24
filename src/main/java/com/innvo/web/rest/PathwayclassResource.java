package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Pathwayclass;

import com.innvo.repository.PathwayclassRepository;
import com.innvo.repository.search.PathwayclassSearchRepository;
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
 * REST controller for managing Pathwayclass.
 */
@RestController
@RequestMapping("/api")
public class PathwayclassResource {

    private final Logger log = LoggerFactory.getLogger(PathwayclassResource.class);

    private static final String ENTITY_NAME = "pathwayclass";

    private final PathwayclassRepository pathwayclassRepository;

    private final PathwayclassSearchRepository pathwayclassSearchRepository;

    public PathwayclassResource(PathwayclassRepository pathwayclassRepository, PathwayclassSearchRepository pathwayclassSearchRepository) {
        this.pathwayclassRepository = pathwayclassRepository;
        this.pathwayclassSearchRepository = pathwayclassSearchRepository;
    }

    /**
     * POST  /pathwayclasses : Create a new pathwayclass.
     *
     * @param pathwayclass the pathwayclass to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pathwayclass, or with status 400 (Bad Request) if the pathwayclass has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/pathwayclasses")
    @Timed
    public ResponseEntity<Pathwayclass> createPathwayclass(@Valid @RequestBody Pathwayclass pathwayclass) throws URISyntaxException {
        log.debug("REST request to save Pathwayclass : {}", pathwayclass);
        if (pathwayclass.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new pathwayclass cannot already have an ID")).body(null);
        }
        Pathwayclass result = pathwayclassRepository.save(pathwayclass);
        pathwayclassSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pathwayclasses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pathwayclasses : Updates an existing pathwayclass.
     *
     * @param pathwayclass the pathwayclass to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pathwayclass,
     * or with status 400 (Bad Request) if the pathwayclass is not valid,
     * or with status 500 (Internal Server Error) if the pathwayclass couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/pathwayclasses")
    @Timed
    public ResponseEntity<Pathwayclass> updatePathwayclass(@Valid @RequestBody Pathwayclass pathwayclass) throws URISyntaxException {
        log.debug("REST request to update Pathwayclass : {}", pathwayclass);
        if (pathwayclass.getId() == null) {
            return createPathwayclass(pathwayclass);
        }
        Pathwayclass result = pathwayclassRepository.save(pathwayclass);
        pathwayclassSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, pathwayclass.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pathwayclasses : get all the pathwayclasses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pathwayclasses in body
     */
    @GetMapping("/pathwayclasses")
    @Timed
    public ResponseEntity<List<Pathwayclass>> getAllPathwayclasses(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Pathwayclasses");
        Page<Pathwayclass> page = pathwayclassRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pathwayclasses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pathwayclasses/:id : get the "id" pathwayclass.
     *
     * @param id the id of the pathwayclass to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pathwayclass, or with status 404 (Not Found)
     */
    @GetMapping("/pathwayclasses/{id}")
    @Timed
    public ResponseEntity<Pathwayclass> getPathwayclass(@PathVariable Long id) {
        log.debug("REST request to get Pathwayclass : {}", id);
        Pathwayclass pathwayclass = pathwayclassRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(pathwayclass));
    }

    /**
     * DELETE  /pathwayclasses/:id : delete the "id" pathwayclass.
     *
     * @param id the id of the pathwayclass to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/pathwayclasses/{id}")
    @Timed
    public ResponseEntity<Void> deletePathwayclass(@PathVariable Long id) {
        log.debug("REST request to delete Pathwayclass : {}", id);
        pathwayclassRepository.delete(id);
        pathwayclassSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/pathwayclasses?query=:query : search for the pathwayclass corresponding
     * to the query.
     *
     * @param query the query of the pathwayclass search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/pathwayclasses")
    @Timed
    public ResponseEntity<List<Pathwayclass>> searchPathwayclasses(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Pathwayclasses for query {}", query);
        Page<Pathwayclass> page = pathwayclassSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pathwayclasses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
