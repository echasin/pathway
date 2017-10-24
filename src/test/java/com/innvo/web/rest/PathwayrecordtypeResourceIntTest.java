package com.innvo.web.rest;

import com.innvo.PathwayApp;

import com.innvo.domain.Pathwayrecordtype;
import com.innvo.repository.PathwayrecordtypeRepository;
import com.innvo.repository.search.PathwayrecordtypeSearchRepository;
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
 * Test class for the PathwayrecordtypeResource REST controller.
 *
 * @see PathwayrecordtypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PathwayApp.class)
public class PathwayrecordtypeResourceIntTest {

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
    private PathwayrecordtypeRepository pathwayrecordtypeRepository;

    @Autowired
    private PathwayrecordtypeSearchRepository pathwayrecordtypeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPathwayrecordtypeMockMvc;

    private Pathwayrecordtype pathwayrecordtype;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PathwayrecordtypeResource pathwayrecordtypeResource = new PathwayrecordtypeResource(pathwayrecordtypeRepository, pathwayrecordtypeSearchRepository);
        this.restPathwayrecordtypeMockMvc = MockMvcBuilders.standaloneSetup(pathwayrecordtypeResource)
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
    public static Pathwayrecordtype createEntity(EntityManager em) {
        Pathwayrecordtype pathwayrecordtype = new Pathwayrecordtype()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return pathwayrecordtype;
    }

    @Before
    public void initTest() {
        pathwayrecordtypeSearchRepository.deleteAll();
        pathwayrecordtype = createEntity(em);
    }

    @Test
    @Transactional
    public void createPathwayrecordtype() throws Exception {
        int databaseSizeBeforeCreate = pathwayrecordtypeRepository.findAll().size();

        // Create the Pathwayrecordtype
        restPathwayrecordtypeMockMvc.perform(post("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayrecordtype)))
            .andExpect(status().isCreated());

        // Validate the Pathwayrecordtype in the database
        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeCreate + 1);
        Pathwayrecordtype testPathwayrecordtype = pathwayrecordtypeList.get(pathwayrecordtypeList.size() - 1);
        assertThat(testPathwayrecordtype.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPathwayrecordtype.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testPathwayrecordtype.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPathwayrecordtype.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPathwayrecordtype.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPathwayrecordtype.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPathwayrecordtype.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Pathwayrecordtype in Elasticsearch
        Pathwayrecordtype pathwayrecordtypeEs = pathwayrecordtypeSearchRepository.findOne(testPathwayrecordtype.getId());
        assertThat(pathwayrecordtypeEs).isEqualToComparingFieldByField(testPathwayrecordtype);
    }

    @Test
    @Transactional
    public void createPathwayrecordtypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pathwayrecordtypeRepository.findAll().size();

        // Create the Pathwayrecordtype with an existing ID
        pathwayrecordtype.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPathwayrecordtypeMockMvc.perform(post("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayrecordtype)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayrecordtypeRepository.findAll().size();
        // set the field null
        pathwayrecordtype.setName(null);

        // Create the Pathwayrecordtype, which fails.

        restPathwayrecordtypeMockMvc.perform(post("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayrecordtype)))
            .andExpect(status().isBadRequest());

        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayrecordtypeRepository.findAll().size();
        // set the field null
        pathwayrecordtype.setNameshort(null);

        // Create the Pathwayrecordtype, which fails.

        restPathwayrecordtypeMockMvc.perform(post("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayrecordtype)))
            .andExpect(status().isBadRequest());

        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayrecordtypeRepository.findAll().size();
        // set the field null
        pathwayrecordtype.setStatus(null);

        // Create the Pathwayrecordtype, which fails.

        restPathwayrecordtypeMockMvc.perform(post("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayrecordtype)))
            .andExpect(status().isBadRequest());

        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayrecordtypeRepository.findAll().size();
        // set the field null
        pathwayrecordtype.setLastmodifiedby(null);

        // Create the Pathwayrecordtype, which fails.

        restPathwayrecordtypeMockMvc.perform(post("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayrecordtype)))
            .andExpect(status().isBadRequest());

        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayrecordtypeRepository.findAll().size();
        // set the field null
        pathwayrecordtype.setLastmodifieddatetime(null);

        // Create the Pathwayrecordtype, which fails.

        restPathwayrecordtypeMockMvc.perform(post("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayrecordtype)))
            .andExpect(status().isBadRequest());

        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayrecordtypeRepository.findAll().size();
        // set the field null
        pathwayrecordtype.setDomain(null);

        // Create the Pathwayrecordtype, which fails.

        restPathwayrecordtypeMockMvc.perform(post("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayrecordtype)))
            .andExpect(status().isBadRequest());

        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPathwayrecordtypes() throws Exception {
        // Initialize the database
        pathwayrecordtypeRepository.saveAndFlush(pathwayrecordtype);

        // Get all the pathwayrecordtypeList
        restPathwayrecordtypeMockMvc.perform(get("/api/pathwayrecordtypes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwayrecordtype.getId().intValue())))
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
    public void getPathwayrecordtype() throws Exception {
        // Initialize the database
        pathwayrecordtypeRepository.saveAndFlush(pathwayrecordtype);

        // Get the pathwayrecordtype
        restPathwayrecordtypeMockMvc.perform(get("/api/pathwayrecordtypes/{id}", pathwayrecordtype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pathwayrecordtype.getId().intValue()))
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
    public void getNonExistingPathwayrecordtype() throws Exception {
        // Get the pathwayrecordtype
        restPathwayrecordtypeMockMvc.perform(get("/api/pathwayrecordtypes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePathwayrecordtype() throws Exception {
        // Initialize the database
        pathwayrecordtypeRepository.saveAndFlush(pathwayrecordtype);
        pathwayrecordtypeSearchRepository.save(pathwayrecordtype);
        int databaseSizeBeforeUpdate = pathwayrecordtypeRepository.findAll().size();

        // Update the pathwayrecordtype
        Pathwayrecordtype updatedPathwayrecordtype = pathwayrecordtypeRepository.findOne(pathwayrecordtype.getId());
        updatedPathwayrecordtype
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restPathwayrecordtypeMockMvc.perform(put("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPathwayrecordtype)))
            .andExpect(status().isOk());

        // Validate the Pathwayrecordtype in the database
        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeUpdate);
        Pathwayrecordtype testPathwayrecordtype = pathwayrecordtypeList.get(pathwayrecordtypeList.size() - 1);
        assertThat(testPathwayrecordtype.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPathwayrecordtype.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testPathwayrecordtype.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPathwayrecordtype.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPathwayrecordtype.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPathwayrecordtype.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPathwayrecordtype.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Pathwayrecordtype in Elasticsearch
        Pathwayrecordtype pathwayrecordtypeEs = pathwayrecordtypeSearchRepository.findOne(testPathwayrecordtype.getId());
        assertThat(pathwayrecordtypeEs).isEqualToComparingFieldByField(testPathwayrecordtype);
    }

    @Test
    @Transactional
    public void updateNonExistingPathwayrecordtype() throws Exception {
        int databaseSizeBeforeUpdate = pathwayrecordtypeRepository.findAll().size();

        // Create the Pathwayrecordtype

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPathwayrecordtypeMockMvc.perform(put("/api/pathwayrecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pathwayrecordtype)))
            .andExpect(status().isCreated());

        // Validate the Pathwayrecordtype in the database
        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePathwayrecordtype() throws Exception {
        // Initialize the database
        pathwayrecordtypeRepository.saveAndFlush(pathwayrecordtype);
        pathwayrecordtypeSearchRepository.save(pathwayrecordtype);
        int databaseSizeBeforeDelete = pathwayrecordtypeRepository.findAll().size();

        // Get the pathwayrecordtype
        restPathwayrecordtypeMockMvc.perform(delete("/api/pathwayrecordtypes/{id}", pathwayrecordtype.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean pathwayrecordtypeExistsInEs = pathwayrecordtypeSearchRepository.exists(pathwayrecordtype.getId());
        assertThat(pathwayrecordtypeExistsInEs).isFalse();

        // Validate the database is empty
        List<Pathwayrecordtype> pathwayrecordtypeList = pathwayrecordtypeRepository.findAll();
        assertThat(pathwayrecordtypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPathwayrecordtype() throws Exception {
        // Initialize the database
        pathwayrecordtypeRepository.saveAndFlush(pathwayrecordtype);
        pathwayrecordtypeSearchRepository.save(pathwayrecordtype);

        // Search the pathwayrecordtype
        restPathwayrecordtypeMockMvc.perform(get("/api/_search/pathwayrecordtypes?query=id:" + pathwayrecordtype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwayrecordtype.getId().intValue())))
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
        TestUtil.equalsVerifier(Pathwayrecordtype.class);
        Pathwayrecordtype pathwayrecordtype1 = new Pathwayrecordtype();
        pathwayrecordtype1.setId(1L);
        Pathwayrecordtype pathwayrecordtype2 = new Pathwayrecordtype();
        pathwayrecordtype2.setId(pathwayrecordtype1.getId());
        assertThat(pathwayrecordtype1).isEqualTo(pathwayrecordtype2);
        pathwayrecordtype2.setId(2L);
        assertThat(pathwayrecordtype1).isNotEqualTo(pathwayrecordtype2);
        pathwayrecordtype1.setId(null);
        assertThat(pathwayrecordtype1).isNotEqualTo(pathwayrecordtype2);
    }
}
