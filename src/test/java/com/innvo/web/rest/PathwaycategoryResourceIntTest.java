package com.innvo.web.rest;

import com.innvo.PathwayApp;

import com.innvo.domain.Pathwaycategory;
import com.innvo.repository.PathwaycategoryRepository;
import com.innvo.repository.search.PathwaycategorySearchRepository;
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
 * Test class for the PathwaycategoryResource REST controller.
 *
 * @see PathwaycategoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PathwayApp.class)
public class PathwaycategoryResourceIntTest {

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
    private PathwaycategoryRepository pathwaycategoryRepository;

    @Autowired
    private PathwaycategorySearchRepository pathwaycategorySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPathwaycategoryMockMvc;

    private Pathwaycategory pathwaycategory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PathwaycategoryResource pathwaycategoryResource = new PathwaycategoryResource(pathwaycategoryRepository, pathwaycategorySearchRepository);
        this.restPathwaycategoryMockMvc = MockMvcBuilders.standaloneSetup(pathwaycategoryResource)
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
    public static Pathwaycategory createEntity(EntityManager em) {
        Pathwaycategory pathwaycategory = new Pathwaycategory()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return pathwaycategory;
    }

    @Before
    public void initTest() {
        pathwaycategorySearchRepository.deleteAll();
        pathwaycategory = createEntity(em);
    }

    @Test
    @Transactional
    public void createPathwaycategory() throws Exception {
        int databaseSizeBeforeCreate = pathwaycategoryRepository.findAll().size();

        // Create the Pathwaycategory
        restPathwaycategoryMockMvc.perform(post("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaycategory)))
            .andExpect(status().isCreated());

        // Validate the Pathwaycategory in the database
        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeCreate + 1);
        Pathwaycategory testPathwaycategory = pathwaycategoryList.get(pathwaycategoryList.size() - 1);
        assertThat(testPathwaycategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPathwaycategory.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testPathwaycategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPathwaycategory.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPathwaycategory.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPathwaycategory.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPathwaycategory.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Pathwaycategory in Elasticsearch
        Pathwaycategory pathwaycategoryEs = pathwaycategorySearchRepository.findOne(testPathwaycategory.getId());
        assertThat(pathwaycategoryEs).isEqualToComparingFieldByField(testPathwaycategory);
    }

    @Test
    @Transactional
    public void createPathwaycategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pathwaycategoryRepository.findAll().size();

        // Create the Pathwaycategory with an existing ID
        pathwaycategory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPathwaycategoryMockMvc.perform(post("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaycategory)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycategoryRepository.findAll().size();
        // set the field null
        pathwaycategory.setName(null);

        // Create the Pathwaycategory, which fails.

        restPathwaycategoryMockMvc.perform(post("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaycategory)))
            .andExpect(status().isBadRequest());

        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycategoryRepository.findAll().size();
        // set the field null
        pathwaycategory.setNameshort(null);

        // Create the Pathwaycategory, which fails.

        restPathwaycategoryMockMvc.perform(post("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaycategory)))
            .andExpect(status().isBadRequest());

        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycategoryRepository.findAll().size();
        // set the field null
        pathwaycategory.setStatus(null);

        // Create the Pathwaycategory, which fails.

        restPathwaycategoryMockMvc.perform(post("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaycategory)))
            .andExpect(status().isBadRequest());

        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycategoryRepository.findAll().size();
        // set the field null
        pathwaycategory.setLastmodifiedby(null);

        // Create the Pathwaycategory, which fails.

        restPathwaycategoryMockMvc.perform(post("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaycategory)))
            .andExpect(status().isBadRequest());

        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycategoryRepository.findAll().size();
        // set the field null
        pathwaycategory.setLastmodifieddatetime(null);

        // Create the Pathwaycategory, which fails.

        restPathwaycategoryMockMvc.perform(post("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaycategory)))
            .andExpect(status().isBadRequest());

        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycategoryRepository.findAll().size();
        // set the field null
        pathwaycategory.setDomain(null);

        // Create the Pathwaycategory, which fails.

        restPathwaycategoryMockMvc.perform(post("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaycategory)))
            .andExpect(status().isBadRequest());

        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPathwaycategories() throws Exception {
        // Initialize the database
        pathwaycategoryRepository.saveAndFlush(pathwaycategory);

        // Get all the pathwaycategoryList
        restPathwaycategoryMockMvc.perform(get("/api/pathwaycategories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaycategory.getId().intValue())))
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
    public void getPathwaycategory() throws Exception {
        // Initialize the database
        pathwaycategoryRepository.saveAndFlush(pathwaycategory);

        // Get the pathwaycategory
        restPathwaycategoryMockMvc.perform(get("/api/pathwaycategories/{id}", pathwaycategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pathwaycategory.getId().intValue()))
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
    public void getNonExistingPathwaycategory() throws Exception {
        // Get the pathwaycategory
        restPathwaycategoryMockMvc.perform(get("/api/pathwaycategories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePathwaycategory() throws Exception {
        // Initialize the database
        pathwaycategoryRepository.saveAndFlush(pathwaycategory);
        pathwaycategorySearchRepository.save(pathwaycategory);
        int databaseSizeBeforeUpdate = pathwaycategoryRepository.findAll().size();

        // Update the pathwaycategory
        Pathwaycategory updatedPathwaycategory = pathwaycategoryRepository.findOne(pathwaycategory.getId());
        updatedPathwaycategory
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restPathwaycategoryMockMvc.perform(put("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPathwaycategory)))
            .andExpect(status().isOk());

        // Validate the Pathwaycategory in the database
        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeUpdate);
        Pathwaycategory testPathwaycategory = pathwaycategoryList.get(pathwaycategoryList.size() - 1);
        assertThat(testPathwaycategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPathwaycategory.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testPathwaycategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPathwaycategory.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPathwaycategory.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPathwaycategory.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPathwaycategory.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Pathwaycategory in Elasticsearch
        Pathwaycategory pathwaycategoryEs = pathwaycategorySearchRepository.findOne(testPathwaycategory.getId());
        assertThat(pathwaycategoryEs).isEqualToComparingFieldByField(testPathwaycategory);
    }

    @Test
    @Transactional
    public void updateNonExistingPathwaycategory() throws Exception {
        int databaseSizeBeforeUpdate = pathwaycategoryRepository.findAll().size();

        // Create the Pathwaycategory

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPathwaycategoryMockMvc.perform(put("/api/pathwaycategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaycategory)))
            .andExpect(status().isCreated());

        // Validate the Pathwaycategory in the database
        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePathwaycategory() throws Exception {
        // Initialize the database
        pathwaycategoryRepository.saveAndFlush(pathwaycategory);
        pathwaycategorySearchRepository.save(pathwaycategory);
        int databaseSizeBeforeDelete = pathwaycategoryRepository.findAll().size();

        // Get the pathwaycategory
        restPathwaycategoryMockMvc.perform(delete("/api/pathwaycategories/{id}", pathwaycategory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean pathwaycategoryExistsInEs = pathwaycategorySearchRepository.exists(pathwaycategory.getId());
        assertThat(pathwaycategoryExistsInEs).isFalse();

        // Validate the database is empty
        List<Pathwaycategory> pathwaycategoryList = pathwaycategoryRepository.findAll();
        assertThat(pathwaycategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPathwaycategory() throws Exception {
        // Initialize the database
        pathwaycategoryRepository.saveAndFlush(pathwaycategory);
        pathwaycategorySearchRepository.save(pathwaycategory);

        // Search the pathwaycategory
        restPathwaycategoryMockMvc.perform(get("/api/_search/pathwaycategories?query=id:" + pathwaycategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaycategory.getId().intValue())))
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
        TestUtil.equalsVerifier(Pathwaycategory.class);
        Pathwaycategory pathwaycategory1 = new Pathwaycategory();
        pathwaycategory1.setId(1L);
        Pathwaycategory pathwaycategory2 = new Pathwaycategory();
        pathwaycategory2.setId(pathwaycategory1.getId());
        assertThat(pathwaycategory1).isEqualTo(pathwaycategory2);
        pathwaycategory2.setId(2L);
        assertThat(pathwaycategory1).isNotEqualTo(pathwaycategory2);
        pathwaycategory1.setId(null);
        assertThat(pathwaycategory1).isNotEqualTo(pathwaycategory2);
    }
}
