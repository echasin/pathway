package com.innvo.web.rest;

import com.innvo.PathwayApp;

import com.innvo.domain.Pathway;
import com.innvo.repository.PathwayRepository;
import com.innvo.repository.search.PathwaySearchRepository;
import com.innvo.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.innvo.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PathwayResource REST controller.
 *
 * @see PathwayResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PathwayApp.class)
public class PathwayResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NAMESHORT = "AAAAAAAAAA";
    private static final String UPDATED_NAMESHORT = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINJSON = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINJSON = "BBBBBBBBBB";

    private static final String DEFAULT_DESTINATIONJSON = "AAAAAAAAAA";
    private static final String UPDATED_DESTINATIONJSON = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_LASTMODIFIEDBY = "AAAAAAAAAA";
    private static final String UPDATED_LASTMODIFIEDBY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LASTMODIFIEDDATETIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LASTMODIFIEDDATETIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DOMAIN = "AAAAAAAAAA";
    private static final String UPDATED_DOMAIN = "BBBBBBBBBB";

    @Autowired
    private PathwayRepository pathwayRepository;

    @Autowired
    private PathwaySearchRepository pathwaySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPathwayMockMvc;

    private Pathway pathway;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PathwayResource pathwayResource = new PathwayResource(pathwayRepository, pathwaySearchRepository);
        this.restPathwayMockMvc = MockMvcBuilders.standaloneSetup(pathwayResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pathway createEntity(EntityManager em) {
        Pathway pathway = new Pathway()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .originjson(DEFAULT_ORIGINJSON)
            .destinationjson(DEFAULT_DESTINATIONJSON)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return pathway;
    }

    @Before
    public void initTest() {
        pathwaySearchRepository.deleteAll();
        pathway = createEntity(em);
    }

    @Test
    @Transactional
    public void createPathway() throws Exception {
        int databaseSizeBeforeCreate = pathwayRepository.findAll().size();

        // Create the Pathway
        restPathwayMockMvc.perform(post("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathway)))
            .andExpect(status().isCreated());

        // Validate the Pathway in the database
        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeCreate + 1);
        Pathway testPathway = pathwayList.get(pathwayList.size() - 1);
        assertThat(testPathway.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPathway.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testPathway.getOriginjson()).isEqualTo(DEFAULT_ORIGINJSON);
        assertThat(testPathway.getDestinationjson()).isEqualTo(DEFAULT_DESTINATIONJSON);
        assertThat(testPathway.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPathway.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPathway.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPathway.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPathway.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Pathway in Elasticsearch
        Pathway pathwayEs = pathwaySearchRepository.findOne(testPathway.getId());
        assertThat(pathwayEs).isEqualToComparingFieldByField(testPathway);
    }

    @Test
    @Transactional
    public void createPathwayWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pathwayRepository.findAll().size();

        // Create the Pathway with an existing ID
        pathway.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPathwayMockMvc.perform(post("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathway)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setName(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathway)))
            .andExpect(status().isBadRequest());

        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setNameshort(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathway)))
            .andExpect(status().isBadRequest());

        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setStatus(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathway)))
            .andExpect(status().isBadRequest());

        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setLastmodifiedby(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathway)))
            .andExpect(status().isBadRequest());

        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setLastmodifieddatetime(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathway)))
            .andExpect(status().isBadRequest());

        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setDomain(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathway)))
            .andExpect(status().isBadRequest());

        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPathways() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);

        // Get all the pathwayList
        restPathwayMockMvc.perform(get("/api/pathways?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathway.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].originjson").value(hasItem(DEFAULT_ORIGINJSON.toString())))
            .andExpect(jsonPath("$.[*].destinationjson").value(hasItem(DEFAULT_DESTINATIONJSON.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getPathway() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);

        // Get the pathway
        restPathwayMockMvc.perform(get("/api/pathways/{id}", pathway.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pathway.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.originjson").value(DEFAULT_ORIGINJSON.toString()))
            .andExpect(jsonPath("$.destinationjson").value(DEFAULT_DESTINATIONJSON.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(sameInstant(DEFAULT_LASTMODIFIEDDATETIME)))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPathway() throws Exception {
        // Get the pathway
        restPathwayMockMvc.perform(get("/api/pathways/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePathway() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);
        pathwaySearchRepository.save(pathway);
        int databaseSizeBeforeUpdate = pathwayRepository.findAll().size();

        // Update the pathway
        Pathway updatedPathway = pathwayRepository.findOne(pathway.getId());
        updatedPathway
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .originjson(UPDATED_ORIGINJSON)
            .destinationjson(UPDATED_DESTINATIONJSON)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restPathwayMockMvc.perform(put("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPathway)))
            .andExpect(status().isOk());

        // Validate the Pathway in the database
        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeUpdate);
        Pathway testPathway = pathwayList.get(pathwayList.size() - 1);
        assertThat(testPathway.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPathway.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testPathway.getOriginjson()).isEqualTo(UPDATED_ORIGINJSON);
        assertThat(testPathway.getDestinationjson()).isEqualTo(UPDATED_DESTINATIONJSON);
        assertThat(testPathway.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPathway.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPathway.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPathway.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPathway.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Pathway in Elasticsearch
        Pathway pathwayEs = pathwaySearchRepository.findOne(testPathway.getId());
        assertThat(pathwayEs).isEqualToComparingFieldByField(testPathway);
    }

    @Test
    @Transactional
    public void updateNonExistingPathway() throws Exception {
        int databaseSizeBeforeUpdate = pathwayRepository.findAll().size();

        // Create the Pathway

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPathwayMockMvc.perform(put("/api/pathways")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathway)))
            .andExpect(status().isCreated());

        // Validate the Pathway in the database
        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePathway() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);
        pathwaySearchRepository.save(pathway);
        int databaseSizeBeforeDelete = pathwayRepository.findAll().size();

        // Get the pathway
        restPathwayMockMvc.perform(delete("/api/pathways/{id}", pathway.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean pathwayExistsInEs = pathwaySearchRepository.exists(pathway.getId());
        assertThat(pathwayExistsInEs).isFalse();

        // Validate the database is empty
        List<Pathway> pathwayList = pathwayRepository.findAll();
        assertThat(pathwayList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPathway() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);
        pathwaySearchRepository.save(pathway);

        // Search the pathway
        restPathwayMockMvc.perform(get("/api/_search/pathways?query=id:" + pathway.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathway.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].originjson").value(hasItem(DEFAULT_ORIGINJSON.toString())))
            .andExpect(jsonPath("$.[*].destinationjson").value(hasItem(DEFAULT_DESTINATIONJSON.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pathway.class);
        Pathway pathway1 = new Pathway();
        pathway1.setId(1L);
        Pathway pathway2 = new Pathway();
        pathway2.setId(pathway1.getId());
        assertThat(pathway1).isEqualTo(pathway2);
        pathway2.setId(2L);
        assertThat(pathway1).isNotEqualTo(pathway2);
        pathway1.setId(null);
        assertThat(pathway1).isNotEqualTo(pathway2);
    }
}
