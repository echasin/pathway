package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Pathwayrecordtype;

import com.innvo.repository.PathwayrecordtypeRepository;
import com.innvo.repository.search.PathwayrecordtypeSearchRepository;
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
 * REST controller for managing Pathwayrecordtype.
 */
@RestController
@RequestMapping("/api")
public class PathwayrecordtypeResource {

    private final Logger log = LoggerFactory.getLogger(PathwayrecordtypeResource.class);

    private static final String ENTITY_NAME = "pathwayrecordtype";

    private final PathwayrecordtypeRepository pathwayrecordtypeRepository;

    private final PathwayrecordtypeSearchRepository pathwayrecordtypeSearchRepository;

    public PathwayrecordtypeResource(PathwayrecordtypeRepository pathwayrecordtypeRepository, PathwayrecordtypeSearchRepository pathwayrecordtypeSearchRepository) {
        this.pathwayrecordtypeRepository = pathwayrecordtypeRepository;
        this.pathwayrecordtypeSearchRepository = pathwayrecordtypeSearchRepository;
    }

    /**
     * POST  /pathwayrecordtypes : Create a new pathwayrecordtype.
     *
     * @param pathwayrecordtype the pathwayrecordtype to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pathwayrecordtype, or with status 400 (Bad Request) if the pathwayrecordtype has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/pathwayrecordtypes")
    @Timed
    public ResponseEntity<Pathwayrecordtype> createPathwayrecordtype(@Valid @RequestBody Pathwayrecordtype pathwayrecordtype) throws URISyntaxException {
        log.debug("REST request to save Pathwayrecordtype : {}", pathwayrecordtype);
        if (pathwayrecordtype.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new pathwayrecordtype cannot already have an ID")).body(null);
        }
        Pathwayrecordtype result = pathwayrecordtypeRepository.save(pathwayrecordtype);
        pathwayrecordtypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pathwayrecordtypes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pathwayrecordtypes : Updates an existing pathwayrecordtype.
     *
     * @param pathwayrecordtype the pathwayrecordtype to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pathwayrecordtype,
     * or with status 400 (Bad Request) if the pathwayrecordtype is not valid,
     * or with status 500 (Internal Server Error) if the pathwayrecordtype couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/pathwayrecordtypes")
    @Timed
    public ResponseEntity<Pathwayrecordtype> updatePathwayrecordtype(@Valid @RequestBody Pathwayrecordtype pathwayrecordtype) throws URISyntaxException {
        log.debug("REST request to update Pathwayrecordtype : {}", pathwayrecordtype);
        if (pathwayrecordtype.getId() == null) {
            return createPathwayrecordtype(pathwayrecordtype);
        }
        Pathwayrecordtype result = pathwayrecordtypeRepository.save(pathwayrecordtype);
        pathwayrecordtypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, pathwayrecordtype.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pathwayrecordtypes : get all the pathwayrecordtypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pathwayrecordtypes in body
     */
    @GetMapping("/pathwayrecordtypes")
    @Timed
    public ResponseEntity<List<Pathwayrecordtype>> getAllPathwayrecordtypes(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Pathwayrecordtypes");
        Page<Pathwayrecordtype> page = pathwayrecordtypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pathwayrecordtypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pathwayrecordtypes/:id : get the "id" pathwayrecordtype.
     *
     * @param id the id of the pathwayrecordtype to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pathwayrecordtype, or with status 404 (Not Found)
     */
    @GetMapping("/pathwayrecordtypes/{id}")
    @Timed
    public ResponseEntity<Pathwayrecordtype> getPathwayrecordtype(@PathVariable Long id) {
        log.debug("REST request to get Pathwayrecordtype : {}", id);
        Pathwayrecordtype pathwayrecordtype = pathwayrecordtypeRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(pathwayrecordtype));
    }

    /**
     * DELETE  /pathwayrecordtypes/:id : delete the "id" pathwayrecordtype.
     *
     * @param id the id of the pathwayrecordtype to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/pathwayrecordtypes/{id}")
    @Timed
    public ResponseEntity<Void> deletePathwayrecordtype(@PathVariable Long id) {
        log.debug("REST request to delete Pathwayrecordtype : {}", id);
        pathwayrecordtypeRepository.delete(id);
        pathwayrecordtypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/pathwayrecordtypes?query=:query : search for the pathwayrecordtype corresponding
     * to the query.
     *
     * @param query the query of the pathwayrecordtype search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/pathwayrecordtypes")
    @Timed
    public ResponseEntity<List<Pathwayrecordtype>> searchPathwayrecordtypes(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Pathwayrecordtypes for query {}", query);
        Page<Pathwayrecordtype> page = pathwayrecordtypeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pathwayrecordtypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
