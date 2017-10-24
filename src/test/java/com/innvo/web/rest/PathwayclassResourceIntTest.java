package com.innvo.web.rest;

import com.innvo.PathwayApp;

import com.innvo.domain.Pathwayclass;
import com.innvo.repository.PathwayclassRepository;
import com.innvo.repository.search.PathwayclassSearchRepository;
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
 * Test class for the PathwayclassResource REST controller.
 *
 * @see PathwayclassResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PathwayApp.class)
public class PathwayclassResourceIntTest {

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
    private PathwayclassRepository pathwayclassRepository;

    @Autowired
    private PathwayclassSearchRepository pathwayclassSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPathwayclassMockMvc;

    private Pathwayclass pathwayclass;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PathwayclassResource pathwayclassResource = new PathwayclassResource(pathwayclassRepository, pathwayclassSearchRepository);
        this.restPathwayclassMockMvc = MockMvcBuilders.standaloneSetup(pathwayclassResource)
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
    public static Pathwayclass createEntity(EntityManager em) {
        Pathwayclass pathwayclass = new Pathwayclass()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return pathwayclass;
    }

    @Before
    public void initTest() {
        pathwayclassSearchRepository.deleteAll();
        pathwayclass = createEntity(em);
    }

    @Test
    @Transactional
    public void createPathwayclass() throws Exception {
        int databaseSizeBeforeCreate = pathwayclassRepository.findAll().size();

        // Create the Pathwayclass
        restPathwayclassMockMvc.perform(post("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayclass)))
            .andExpect(status().isCreated());

        // Validate the Pathwayclass in the database
        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeCreate + 1);
        Pathwayclass testPathwayclass = pathwayclassList.get(pathwayclassList.size() - 1);
        assertThat(testPathwayclass.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPathwayclass.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testPathwayclass.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPathwayclass.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPathwayclass.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPathwayclass.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPathwayclass.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Pathwayclass in Elasticsearch
        Pathwayclass pathwayclassEs = pathwayclassSearchRepository.findOne(testPathwayclass.getId());
        assertThat(pathwayclassEs).isEqualToComparingFieldByField(testPathwayclass);
    }

    @Test
    @Transactional
    public void createPathwayclassWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pathwayclassRepository.findAll().size();

        // Create the Pathwayclass with an existing ID
        pathwayclass.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPathwayclassMockMvc.perform(post("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayclass)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayclassRepository.findAll().size();
        // set the field null
        pathwayclass.setName(null);

        // Create the Pathwayclass, which fails.

        restPathwayclassMockMvc.perform(post("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayclass)))
            .andExpect(status().isBadRequest());

        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayclassRepository.findAll().size();
        // set the field null
        pathwayclass.setNameshort(null);

        // Create the Pathwayclass, which fails.

        restPathwayclassMockMvc.perform(post("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayclass)))
            .andExpect(status().isBadRequest());

        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayclassRepository.findAll().size();
        // set the field null
        pathwayclass.setStatus(null);

        // Create the Pathwayclass, which fails.

        restPathwayclassMockMvc.perform(post("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayclass)))
            .andExpect(status().isBadRequest());

        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayclassRepository.findAll().size();
        // set the field null
        pathwayclass.setLastmodifiedby(null);

        // Create the Pathwayclass, which fails.

        restPathwayclassMockMvc.perform(post("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayclass)))
            .andExpect(status().isBadRequest());

        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayclassRepository.findAll().size();
        // set the field null
        pathwayclass.setLastmodifieddatetime(null);

        // Create the Pathwayclass, which fails.

        restPathwayclassMockMvc.perform(post("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayclass)))
            .andExpect(status().isBadRequest());

        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayclassRepository.findAll().size();
        // set the field null
        pathwayclass.setDomain(null);

        // Create the Pathwayclass, which fails.

        restPathwayclassMockMvc.perform(post("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayclass)))
            .andExpect(status().isBadRequest());

        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPathwayclasses() throws Exception {
        // Initialize the database
        pathwayclassRepository.saveAndFlush(pathwayclass);

        // Get all the pathwayclassList
        restPathwayclassMockMvc.perform(get("/api/pathwayclasses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwayclass.getId().intValue())))
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
    public void getPathwayclass() throws Exception {
        // Initialize the database
        pathwayclassRepository.saveAndFlush(pathwayclass);

        // Get the pathwayclass
        restPathwayclassMockMvc.perform(get("/api/pathwayclasses/{id}", pathwayclass.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pathwayclass.getId().intValue()))
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
    public void getNonExistingPathwayclass() throws Exception {
        // Get the pathwayclass
        restPathwayclassMockMvc.perform(get("/api/pathwayclasses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePathwayclass() throws Exception {
        // Initialize the database
        pathwayclassRepository.saveAndFlush(pathwayclass);
        pathwayclassSearchRepository.save(pathwayclass);
        int databaseSizeBeforeUpdate = pathwayclassRepository.findAll().size();

        // Update the pathwayclass
        Pathwayclass updatedPathwayclass = pathwayclassRepository.findOne(pathwayclass.getId());
        updatedPathwayclass
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restPathwayclassMockMvc.perform(put("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPathwayclass)))
            .andExpect(status().isOk());

        // Validate the Pathwayclass in the database
        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeUpdate);
        Pathwayclass testPathwayclass = pathwayclassList.get(pathwayclassList.size() - 1);
        assertThat(testPathwayclass.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPathwayclass.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testPathwayclass.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPathwayclass.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPathwayclass.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPathwayclass.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPathwayclass.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Pathwayclass in Elasticsearch
        Pathwayclass pathwayclassEs = pathwayclassSearchRepository.findOne(testPathwayclass.getId());
        assertThat(pathwayclassEs).isEqualToComparingFieldByField(testPathwayclass);
    }

    @Test
    @Transactional
    public void updateNonExistingPathwayclass() throws Exception {
        int databaseSizeBeforeUpdate = pathwayclassRepository.findAll().size();

        // Create the Pathwayclass

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPathwayclassMockMvc.perform(put("/api/pathwayclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayclass)))
            .andExpect(status().isCreated());

        // Validate the Pathwayclass in the database
        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePathwayclass() throws Exception {
        // Initialize the database
        pathwayclassRepository.saveAndFlush(pathwayclass);
        pathwayclassSearchRepository.save(pathwayclass);
        int databaseSizeBeforeDelete = pathwayclassRepository.findAll().size();

        // Get the pathwayclass
        restPathwayclassMockMvc.perform(delete("/api/pathwayclasses/{id}", pathwayclass.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean pathwayclassExistsInEs = pathwayclassSearchRepository.exists(pathwayclass.getId());
        assertThat(pathwayclassExistsInEs).isFalse();

        // Validate the database is empty
        List<Pathwayclass> pathwayclassList = pathwayclassRepository.findAll();
        assertThat(pathwayclassList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPathwayclass() throws Exception {
        // Initialize the database
        pathwayclassRepository.saveAndFlush(pathwayclass);
        pathwayclassSearchRepository.save(pathwayclass);

        // Search the pathwayclass
        restPathwayclassMockMvc.perform(get("/api/_search/pathwayclasses?query=id:" + pathwayclass.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwayclass.getId().intValue())))
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
        TestUtil.equalsVerifier(Pathwayclass.class);
        Pathwayclass pathwayclass1 = new Pathwayclass();
        pathwayclass1.setId(1L);
        Pathwayclass pathwayclass2 = new Pathwayclass();
        pathwayclass2.setId(pathwayclass1.getId());
        assertThat(pathwayclass1).isEqualTo(pathwayclass2);
        pathwayclass2.setId(2L);
        assertThat(pathwayclass1).isNotEqualTo(pathwayclass2);
        pathwayclass1.setId(null);
        assertThat(pathwayclass1).isNotEqualTo(pathwayclass2);
    }
}
