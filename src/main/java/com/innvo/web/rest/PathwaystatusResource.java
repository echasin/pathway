package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Pathwaystatus;

import com.innvo.repository.PathwaystatusRepository;
import com.innvo.repository.search.PathwaystatusSearchRepository;
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
 * REST controller for managing Pathwaystatus.
 */
@RestController
@RequestMapping("/api")
public class PathwaystatusResource {

    private final Logger log = LoggerFactory.getLogger(PathwaystatusResource.class);

    private static final String ENTITY_NAME = "pathwaystatus";

    private final PathwaystatusRepository pathwaystatusRepository;

    private final PathwaystatusSearchRepository pathwaystatusSearchRepository;

    public PathwaystatusResource(PathwaystatusRepository pathwaystatusRepository, PathwaystatusSearchRepository pathwaystatusSearchRepository) {
        this.pathwaystatusRepository = pathwaystatusRepository;
        this.pathwaystatusSearchRepository = pathwaystatusSearchRepository;
    }

    /**
     * POST  /pathwaystatuses : Create a new pathwaystatus.
     *
     * @param pathwaystatus the pathwaystatus to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pathwaystatus, or with status 400 (Bad Request) if the pathwaystatus has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/pathwaystatuses")
    @Timed
    public ResponseEntity<Pathwaystatus> createPathwaystatus(@Valid @RequestBody Pathwaystatus pathwaystatus) throws URISyntaxException {
        log.debug("REST request to save Pathwaystatus : {}", pathwaystatus);
        if (pathwaystatus.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new pathwaystatus cannot already have an ID")).body(null);
        }
        Pathwaystatus result = pathwaystatusRepository.save(pathwaystatus);
        pathwaystatusSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pathwaystatuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pathwaystatuses : Updates an existing pathwaystatus.
     *
     * @param pathwaystatus the pathwaystatus to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pathwaystatus,
     * or with status 400 (Bad Request) if the pathwaystatus is not valid,
     * or with status 500 (Internal Server Error) if the pathwaystatus couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/pathwaystatuses")
    @Timed
    public ResponseEntity<Pathwaystatus> updatePathwaystatus(@Valid @RequestBody Pathwaystatus pathwaystatus) throws URISyntaxException {
        log.debug("REST request to update Pathwaystatus : {}", pathwaystatus);
        if (pathwaystatus.getId() == null) {
            return createPathwaystatus(pathwaystatus);
        }
        Pathwaystatus result = pathwaystatusRepository.save(pathwaystatus);
        pathwaystatusSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, pathwaystatus.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pathwaystatuses : get all the pathwaystatuses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pathwaystatuses in body
     */
    @GetMapping("/pathwaystatuses")
    @Timed
    public ResponseEntity<List<Pathwaystatus>> getAllPathwaystatuses(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Pathwaystatuses");
        Page<Pathwaystatus> page = pathwaystatusRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pathwaystatuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pathwaystatuses/:id : get the "id" pathwaystatus.
     *
     * @param id the id of the pathwaystatus to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pathwaystatus, or with status 404 (Not Found)
     */
    @GetMapping("/pathwaystatuses/{id}")
    @Timed
    public ResponseEntity<Pathwaystatus> getPathwaystatus(@PathVariable Long id) {
        log.debug("REST request to get Pathwaystatus : {}", id);
        Pathwaystatus pathwaystatus = pathwaystatusRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(pathwaystatus));
    }

    /**
     * DELETE  /pathwaystatuses/:id : delete the "id" pathwaystatus.
     *
     * @param id the id of the pathwaystatus to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/pathwaystatuses/{id}")
    @Timed
    public ResponseEntity<Void> deletePathwaystatus(@PathVariable Long id) {
        log.debug("REST request to delete Pathwaystatus : {}", id);
        pathwaystatusRepository.delete(id);
        pathwaystatusSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/pathwaystatuses?query=:query : search for the pathwaystatus corresponding
     * to the query.
     *
     * @param query the query of the pathwaystatus search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/pathwaystatuses")
    @Timed
    public ResponseEntity<List<Pathwaystatus>> searchPathwaystatuses(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Pathwaystatuses for query {}", query);
        Page<Pathwaystatus> page = pathwaystatusSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pathwaystatuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
