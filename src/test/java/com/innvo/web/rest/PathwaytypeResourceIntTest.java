package com.innvo.web.rest;

import com.innvo.PathwayApp;

import com.innvo.domain.Pathwaytype;
import com.innvo.repository.PathwaytypeRepository;
import com.innvo.repository.search.PathwaytypeSearchRepository;
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
 * Test class for the PathwaytypeResource REST controller.
 *
 * @see PathwaytypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PathwayApp.class)
public class PathwaytypeResourceIntTest {

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
    private PathwaytypeRepository pathwaytypeRepository;

    @Autowired
    private PathwaytypeSearchRepository pathwaytypeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPathwaytypeMockMvc;

    private Pathwaytype pathwaytype;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PathwaytypeResource pathwaytypeResource = new PathwaytypeResource(pathwaytypeRepository, pathwaytypeSearchRepository);
        this.restPathwaytypeMockMvc = MockMvcBuilders.standaloneSetup(pathwaytypeResource)
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
    public static Pathwaytype createEntity(EntityManager em) {
        Pathwaytype pathwaytype = new Pathwaytype()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return pathwaytype;
    }

    @Before
    public void initTest() {
        pathwaytypeSearchRepository.deleteAll();
        pathwaytype = createEntity(em);
    }

    @Test
    @Transactional
    public void createPathwaytype() throws Exception {
        int databaseSizeBeforeCreate = pathwaytypeRepository.findAll().size();

        // Create the Pathwaytype
        restPathwaytypeMockMvc.perform(post("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaytype)))
            .andExpect(status().isCreated());

        // Validate the Pathwaytype in the database
        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeCreate + 1);
        Pathwaytype testPathwaytype = pathwaytypeList.get(pathwaytypeList.size() - 1);
        assertThat(testPathwaytype.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPathwaytype.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testPathwaytype.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPathwaytype.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPathwaytype.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPathwaytype.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPathwaytype.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Pathwaytype in Elasticsearch
        Pathwaytype pathwaytypeEs = pathwaytypeSearchRepository.findOne(testPathwaytype.getId());
        assertThat(pathwaytypeEs).isEqualToComparingFieldByField(testPathwaytype);
    }

    @Test
    @Transactional
    public void createPathwaytypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pathwaytypeRepository.findAll().size();

        // Create the Pathwaytype with an existing ID
        pathwaytype.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPathwaytypeMockMvc.perform(post("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaytype)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaytypeRepository.findAll().size();
        // set the field null
        pathwaytype.setName(null);

        // Create the Pathwaytype, which fails.

        restPathwaytypeMockMvc.perform(post("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaytype)))
            .andExpect(status().isBadRequest());

        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaytypeRepository.findAll().size();
        // set the field null
        pathwaytype.setNameshort(null);

        // Create the Pathwaytype, which fails.

        restPathwaytypeMockMvc.perform(post("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaytype)))
            .andExpect(status().isBadRequest());

        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaytypeRepository.findAll().size();
        // set the field null
        pathwaytype.setStatus(null);

        // Create the Pathwaytype, which fails.

        restPathwaytypeMockMvc.perform(post("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaytype)))
            .andExpect(status().isBadRequest());

        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaytypeRepository.findAll().size();
        // set the field null
        pathwaytype.setLastmodifiedby(null);

        // Create the Pathwaytype, which fails.

        restPathwaytypeMockMvc.perform(post("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaytype)))
            .andExpect(status().isBadRequest());

        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaytypeRepository.findAll().size();
        // set the field null
        pathwaytype.setLastmodifieddatetime(null);

        // Create the Pathwaytype, which fails.

        restPathwaytypeMockMvc.perform(post("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaytype)))
            .andExpect(status().isBadRequest());

        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaytypeRepository.findAll().size();
        // set the field null
        pathwaytype.setDomain(null);

        // Create the Pathwaytype, which fails.

        restPathwaytypeMockMvc.perform(post("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaytype)))
            .andExpect(status().isBadRequest());

        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPathwaytypes() throws Exception {
        // Initialize the database
        pathwaytypeRepository.saveAndFlush(pathwaytype);

        // Get all the pathwaytypeList
        restPathwaytypeMockMvc.perform(get("/api/pathwaytypes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaytype.getId().intValue())))
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
    public void getPathwaytype() throws Exception {
        // Initialize the database
        pathwaytypeRepository.saveAndFlush(pathwaytype);

        // Get the pathwaytype
        restPathwaytypeMockMvc.perform(get("/api/pathwaytypes/{id}", pathwaytype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pathwaytype.getId().intValue()))
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
    public void getNonExistingPathwaytype() throws Exception {
        // Get the pathwaytype
        restPathwaytypeMockMvc.perform(get("/api/pathwaytypes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePathwaytype() throws Exception {
        // Initialize the database
        pathwaytypeRepository.saveAndFlush(pathwaytype);
        pathwaytypeSearchRepository.save(pathwaytype);
        int databaseSizeBeforeUpdate = pathwaytypeRepository.findAll().size();

        // Update the pathwaytype
        Pathwaytype updatedPathwaytype = pathwaytypeRepository.findOne(pathwaytype.getId());
        updatedPathwaytype
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restPathwaytypeMockMvc.perform(put("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPathwaytype)))
            .andExpect(status().isOk());

        // Validate the Pathwaytype in the database
        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeUpdate);
        Pathwaytype testPathwaytype = pathwaytypeList.get(pathwaytypeList.size() - 1);
        assertThat(testPathwaytype.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPathwaytype.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testPathwaytype.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPathwaytype.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPathwaytype.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPathwaytype.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPathwaytype.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Pathwaytype in Elasticsearch
        Pathwaytype pathwaytypeEs = pathwaytypeSearchRepository.findOne(testPathwaytype.getId());
        assertThat(pathwaytypeEs).isEqualToComparingFieldByField(testPathwaytype);
    }

    @Test
    @Transactional
    public void updateNonExistingPathwaytype() throws Exception {
        int databaseSizeBeforeUpdate = pathwaytypeRepository.findAll().size();

        // Create the Pathwaytype

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPathwaytypeMockMvc.perform(put("/api/pathwaytypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwaytype)))
            .andExpect(status().isCreated());

        // Validate the Pathwaytype in the database
        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePathwaytype() throws Exception {
        // Initialize the database
        pathwaytypeRepository.saveAndFlush(pathwaytype);
        pathwaytypeSearchRepository.save(pathwaytype);
        int databaseSizeBeforeDelete = pathwaytypeRepository.findAll().size();

        // Get the pathwaytype
        restPathwaytypeMockMvc.perform(delete("/api/pathwaytypes/{id}", pathwaytype.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean pathwaytypeExistsInEs = pathwaytypeSearchRepository.exists(pathwaytype.getId());
        assertThat(pathwaytypeExistsInEs).isFalse();

        // Validate the database is empty
        List<Pathwaytype> pathwaytypeList = pathwaytypeRepository.findAll();
        assertThat(pathwaytypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPathwaytype() throws Exception {
        // Initialize the database
        pathwaytypeRepository.saveAndFlush(pathwaytype);
        pathwaytypeSearchRepository.save(pathwaytype);

        // Search the pathwaytype
        restPathwaytypeMockMvc.perform(get("/api/_search/pathwaytypes?query=id:" + pathwaytype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaytype.getId().intValue())))
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
        TestUtil.equalsVerifier(Pathwaytype.class);
        Pathwaytype pathwaytype1 = new Pathwaytype();
        pathwaytype1.setId(1L);
        Pathwaytype pathwaytype2 = new Pathwaytype();
        pathwaytype2.setId(pathwaytype1.getId());
        assertThat(pathwaytype1).isEqualTo(pathwaytype2);
        pathwaytype2.setId(2L);
        assertThat(pathwaytype1).isNotEqualTo(pathwaytype2);
        pathwaytype1.setId(null);
        assertThat(pathwaytype1).isNotEqualTo(pathwaytype2);
    }
}
