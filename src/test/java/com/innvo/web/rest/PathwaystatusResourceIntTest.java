package com.innvo.web.rest;

import com.innvo.PathwayApp;

import com.innvo.domain.Pathwaystatus;
import com.innvo.repository.PathwaystatusRepository;
import com.innvo.repository.search.PathwaystatusSearchRepository;
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
 * Test class for the PathwaystatusResource REST controller.
 *
 * @see PathwaystatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PathwayApp.class)
public class PathwaystatusResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NAMESHORT = "AAAAAAAAAA";
    private static final String UPDATED_NAMESHORT = "BBBBBBBBBB";

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
    private PathwaystatusRepository pathwaystatusRepository;

    @Autowired
    private PathwaystatusSearchRepository pathwaystatusSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPathwaystatusMockMvc;

    private Pathwaystatus pathwaystatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PathwaystatusResource pathwaystatusResource = new PathwaystatusResource(pathwaystatusRepository, pathwaystatusSearchRepository);
        this.restPathwaystatusMockMvc = MockMvcBuilders.standaloneSetup(pathwaystatusResource)
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
    public static Pathwaystatus createEntity(EntityManager em) {
        Pathwaystatus pathwaystatus = new Pathwaystatus()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return pathwaystatus;
    }

    @Before
    public void initTest() {
        pathwaystatusSearchRepository.deleteAll();
        pathwaystatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createPathwaystatus() throws Exception {
        int databaseSizeBeforeCreate = pathwaystatusRepository.findAll().size();

        // Create the Pathwaystatus
        restPathwaystatusMockMvc.perform(post("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaystatus)))
            .andExpect(status().isCreated());

        // Validate the Pathwaystatus in the database
        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeCreate + 1);
        Pathwaystatus testPathwaystatus = pathwaystatusList.get(pathwaystatusList.size() - 1);
        assertThat(testPathwaystatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPathwaystatus.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testPathwaystatus.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPathwaystatus.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPathwaystatus.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPathwaystatus.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPathwaystatus.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Pathwaystatus in Elasticsearch
        Pathwaystatus pathwaystatusEs = pathwaystatusSearchRepository.findOne(testPathwaystatus.getId());
        assertThat(pathwaystatusEs).isEqualToComparingFieldByField(testPathwaystatus);
    }

    @Test
    @Transactional
    public void createPathwaystatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pathwaystatusRepository.findAll().size();

        // Create the Pathwaystatus with an existing ID
        pathwaystatus.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPathwaystatusMockMvc.perform(post("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaystatus)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaystatusRepository.findAll().size();
        // set the field null
        pathwaystatus.setName(null);

        // Create the Pathwaystatus, which fails.

        restPathwaystatusMockMvc.perform(post("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaystatus)))
            .andExpect(status().isBadRequest());

        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaystatusRepository.findAll().size();
        // set the field null
        pathwaystatus.setNameshort(null);

        // Create the Pathwaystatus, which fails.

        restPathwaystatusMockMvc.perform(post("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaystatus)))
            .andExpect(status().isBadRequest());

        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaystatusRepository.findAll().size();
        // set the field null
        pathwaystatus.setStatus(null);

        // Create the Pathwaystatus, which fails.

        restPathwaystatusMockMvc.perform(post("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaystatus)))
            .andExpect(status().isBadRequest());

        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaystatusRepository.findAll().size();
        // set the field null
        pathwaystatus.setLastmodifiedby(null);

        // Create the Pathwaystatus, which fails.

        restPathwaystatusMockMvc.perform(post("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaystatus)))
            .andExpect(status().isBadRequest());

        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaystatusRepository.findAll().size();
        // set the field null
        pathwaystatus.setLastmodifieddatetime(null);

        // Create the Pathwaystatus, which fails.

        restPathwaystatusMockMvc.perform(post("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaystatus)))
            .andExpect(status().isBadRequest());

        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaystatusRepository.findAll().size();
        // set the field null
        pathwaystatus.setDomain(null);

        // Create the Pathwaystatus, which fails.

        restPathwaystatusMockMvc.perform(post("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaystatus)))
            .andExpect(status().isBadRequest());

        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPathwaystatuses() throws Exception {
        // Initialize the database
        pathwaystatusRepository.saveAndFlush(pathwaystatus);

        // Get all the pathwaystatusList
        restPathwaystatusMockMvc.perform(get("/api/pathwaystatuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaystatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getPathwaystatus() throws Exception {
        // Initialize the database
        pathwaystatusRepository.saveAndFlush(pathwaystatus);

        // Get the pathwaystatus
        restPathwaystatusMockMvc.perform(get("/api/pathwaystatuses/{id}", pathwaystatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pathwaystatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(sameInstant(DEFAULT_LASTMODIFIEDDATETIME)))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPathwaystatus() throws Exception {
        // Get the pathwaystatus
        restPathwaystatusMockMvc.perform(get("/api/pathwaystatuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePathwaystatus() throws Exception {
        // Initialize the database
        pathwaystatusRepository.saveAndFlush(pathwaystatus);
        pathwaystatusSearchRepository.save(pathwaystatus);
        int databaseSizeBeforeUpdate = pathwaystatusRepository.findAll().size();

        // Update the pathwaystatus
        Pathwaystatus updatedPathwaystatus = pathwaystatusRepository.findOne(pathwaystatus.getId());
        updatedPathwaystatus
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restPathwaystatusMockMvc.perform(put("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPathwaystatus)))
            .andExpect(status().isOk());

        // Validate the Pathwaystatus in the database
        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeUpdate);
        Pathwaystatus testPathwaystatus = pathwaystatusList.get(pathwaystatusList.size() - 1);
        assertThat(testPathwaystatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPathwaystatus.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testPathwaystatus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPathwaystatus.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPathwaystatus.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPathwaystatus.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPathwaystatus.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Pathwaystatus in Elasticsearch
        Pathwaystatus pathwaystatusEs = pathwaystatusSearchRepository.findOne(testPathwaystatus.getId());
        assertThat(pathwaystatusEs).isEqualToComparingFieldByField(testPathwaystatus);
    }

    @Test
    @Transactional
    public void updateNonExistingPathwaystatus() throws Exception {
        int databaseSizeBeforeUpdate = pathwaystatusRepository.findAll().size();

        // Create the Pathwaystatus

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPathwaystatusMockMvc.perform(put("/api/pathwaystatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaystatus)))
            .andExpect(status().isCreated());

        // Validate the Pathwaystatus in the database
        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePathwaystatus() throws Exception {
        // Initialize the database
        pathwaystatusRepository.saveAndFlush(pathwaystatus);
        pathwaystatusSearchRepository.save(pathwaystatus);
        int databaseSizeBeforeDelete = pathwaystatusRepository.findAll().size();

        // Get the pathwaystatus
        restPathwaystatusMockMvc.perform(delete("/api/pathwaystatuses/{id}", pathwaystatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean pathwaystatusExistsInEs = pathwaystatusSearchRepository.exists(pathwaystatus.getId());
        assertThat(pathwaystatusExistsInEs).isFalse();

        // Validate the database is empty
        List<Pathwaystatus> pathwaystatusList = pathwaystatusRepository.findAll();
        assertThat(pathwaystatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPathwaystatus() throws Exception {
        // Initialize the database
        pathwaystatusRepository.saveAndFlush(pathwaystatus);
        pathwaystatusSearchRepository.save(pathwaystatus);

        // Search the pathwaystatus
        restPathwaystatusMockMvc.perform(get("/api/_search/pathwaystatuses?query=id:" + pathwaystatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaystatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pathwaystatus.class);
        Pathwaystatus pathwaystatus1 = new Pathwaystatus();
        pathwaystatus1.setId(1L);
        Pathwaystatus pathwaystatus2 = new Pathwaystatus();
        pathwaystatus2.setId(pathwaystatus1.getId());
        assertThat(pathwaystatus1).isEqualTo(pathwaystatus2);
        pathwaystatus2.setId(2L);
        assertThat(pathwaystatus1).isNotEqualTo(pathwaystatus2);
        pathwaystatus1.setId(null);
        assertThat(pathwaystatus1).isNotEqualTo(pathwaystatus2);
    }
}
