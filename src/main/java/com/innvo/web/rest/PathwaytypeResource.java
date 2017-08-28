package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Pathwaytype;

import com.innvo.repository.PathwaytypeRepository;
import com.innvo.repository.search.PathwaytypeSearchRepository;
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
 * REST controller for managing Pathwaytype.
 */
@RestController
@RequestMapping("/api")
public class PathwaytypeResource {

    private final Logger log = LoggerFactory.getLogger(PathwaytypeResource.class);

    private static final String ENTITY_NAME = "pathwaytype";

    private final PathwaytypeRepository pathwaytypeRepository;

    private final PathwaytypeSearchRepository pathwaytypeSearchRepository;

    public PathwaytypeResource(PathwaytypeRepository pathwaytypeRepository, PathwaytypeSearchRepository pathwaytypeSearchRepository) {
        this.pathwaytypeRepository = pathwaytypeRepository;
        this.pathwaytypeSearchRepository = pathwaytypeSearchRepository;
    }

    /**
     * POST  /pathwaytypes : Create a new pathwaytype.
     *
     * @param pathwaytype the pathwaytype to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pathwaytype, or with status 400 (Bad Request) if the pathwaytype has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/pathwaytypes")
    @Timed
    public ResponseEntity<Pathwaytype> createPathwaytype(@Valid @RequestBody Pathwaytype pathwaytype) throws URISyntaxException {
        log.debug("REST request to save Pathwaytype : {}", pathwaytype);
        if (pathwaytype.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new pathwaytype cannot already have an ID")).body(null);
        }
        Pathwaytype result = pathwaytypeRepository.save(pathwaytype);
        pathwaytypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pathwaytypes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pathwaytypes : Updates an existing pathwaytype.
     *
     * @param pathwaytype the pathwaytype to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pathwaytype,
     * or with status 400 (Bad Request) if the pathwaytype is not valid,
     * or with status 500 (Internal Server Error) if the pathwaytype couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/pathwaytypes")
    @Timed
    public ResponseEntity<Pathwaytype> updatePathwaytype(@Valid @RequestBody Pathwaytype pathwaytype) throws URISyntaxException {
        log.debug("REST request to update Pathwaytype : {}", pathwaytype);
        if (pathwaytype.getId() == null) {
            return createPathwaytype(pathwaytype);
        }
        Pathwaytype result = pathwaytypeRepository.save(pathwaytype);
        pathwaytypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, pathwaytype.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pathwaytypes : get all the pathwaytypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pathwaytypes in body
     */
    @GetMapping("/pathwaytypes")
    @Timed
    public ResponseEntity<List<Pathwaytype>> getAllPathwaytypes(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Pathwaytypes");
        Page<Pathwaytype> page = pathwaytypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pathwaytypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pathwaytypes/:id : get the "id" pathwaytype.
     *
     * @param id the id of the pathwaytype to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pathwaytype, or with status 404 (Not Found)
     */
    @GetMapping("/pathwaytypes/{id}")
    @Timed
    public ResponseEntity<Pathwaytype> getPathwaytype(@PathVariable Long id) {
        log.debug("REST request to get Pathwaytype : {}", id);
        Pathwaytype pathwaytype = pathwaytypeRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(pathwaytype));
    }

    /**
     * DELETE  /pathwaytypes/:id : delete the "id" pathwaytype.
     *
     * @param id the id of the pathwaytype to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/pathwaytypes/{id}")
    @Timed
    public ResponseEntity<Void> deletePathwaytype(@PathVariable Long id) {
        log.debug("REST request to delete Pathwaytype : {}", id);
        pathwaytypeRepository.delete(id);
        pathwaytypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/pathwaytypes?query=:query : search for the pathwaytype corresponding
     * to the query.
     *
     * @param query the query of the pathwaytype search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/pathwaytypes")
    @Timed
    public ResponseEntity<List<Pathwaytype>> searchPathwaytypes(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Pathwaytypes for query {}", query);
        Page<Pathwaytype> page = pathwaytypeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pathwaytypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
