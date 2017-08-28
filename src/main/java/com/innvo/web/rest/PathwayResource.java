package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Pathway;

import com.innvo.repository.PathwayRepository;
import com.innvo.repository.search.PathwaySearchRepository;
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
 * REST controller for managing Pathway.
 */
@RestController
@RequestMapping("/api")
public class PathwayResource {

    private final Logger log = LoggerFactory.getLogger(PathwayResource.class);

    private static final String ENTITY_NAME = "pathway";

    private final PathwayRepository pathwayRepository;

    private final PathwaySearchRepository pathwaySearchRepository;

    public PathwayResource(PathwayRepository pathwayRepository, PathwaySearchRepository pathwaySearchRepository) {
        this.pathwayRepository = pathwayRepository;
        this.pathwaySearchRepository = pathwaySearchRepository;
    }

    /**
     * POST  /pathways : Create a new pathway.
     *
     * @param pathway the pathway to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pathway, or with status 400 (Bad Request) if the pathway has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/pathways")
    @Timed
    public ResponseEntity<Pathway> createPathway(@Valid @RequestBody Pathway pathway) throws URISyntaxException {
        log.debug("REST request to save Pathway : {}", pathway);
        if (pathway.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new pathway cannot already have an ID")).body(null);
        }
        Pathway result = pathwayRepository.save(pathway);
        pathwaySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pathways/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pathways : Updates an existing pathway.
     *
     * @param pathway the pathway to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pathway,
     * or with status 400 (Bad Request) if the pathway is not valid,
     * or with status 500 (Internal Server Error) if the pathway couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/pathways")
    @Timed
    public ResponseEntity<Pathway> updatePathway(@Valid @RequestBody Pathway pathway) throws URISyntaxException {
        log.debug("REST request to update Pathway : {}", pathway);
        if (pathway.getId() == null) {
            return createPathway(pathway);
        }
        Pathway result = pathwayRepository.save(pathway);
        pathwaySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, pathway.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pathways : get all the pathways.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pathways in body
     */
    @GetMapping("/pathways")
    @Timed
    public ResponseEntity<List<Pathway>> getAllPathways(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Pathways");
        Page<Pathway> page = pathwayRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pathways");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pathways/:id : get the "id" pathway.
     *
     * @param id the id of the pathway to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pathway, or with status 404 (Not Found)
     */
    @GetMapping("/pathways/{id}")
    @Timed
    public ResponseEntity<Pathway> getPathway(@PathVariable Long id) {
        log.debug("REST request to get Pathway : {}", id);
        Pathway pathway = pathwayRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(pathway));
    }

    /**
     * DELETE  /pathways/:id : delete the "id" pathway.
     *
     * @param id the id of the pathway to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/pathways/{id}")
    @Timed
    public ResponseEntity<Void> deletePathway(@PathVariable Long id) {
        log.debug("REST request to delete Pathway : {}", id);
        pathwayRepository.delete(id);
        pathwaySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/pathways?query=:query : search for the pathway corresponding
     * to the query.
     *
     * @param query the query of the pathway search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/pathways")
    @Timed
    public ResponseEntity<List<Pathway>> searchPathways(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Pathways for query {}", query);
        Page<Pathway> page = pathwaySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pathways");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
