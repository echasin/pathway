package com.innvo.web.rest;

import com.innvo.PathwayApp;

import com.innvo.domain.Vector;
import com.innvo.repository.VectorRepository;
import com.innvo.repository.search.VectorSearchRepository;
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
 * Test class for the VectorResource REST controller.
 *
 * @see VectorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PathwayApp.class)
public class VectorResourceIntTest {

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
    private VectorRepository vectorRepository;

    @Autowired
    private VectorSearchRepository vectorSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restVectorMockMvc;

    private Vector vector;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        VectorResource vectorResource = new VectorResource(vectorRepository, vectorSearchRepository);
        this.restVectorMockMvc = MockMvcBuilders.standaloneSetup(vectorResource)
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
    public static Vector createEntity(EntityManager em) {
        Vector vector = new Vector()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .originjson(DEFAULT_ORIGINJSON)
            .destinationjson(DEFAULT_DESTINATIONJSON)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return vector;
    }

    @Before
    public void initTest() {
        vectorSearchRepository.deleteAll();
        vector = createEntity(em);
    }

    @Test
    @Transactional
    public void createVector() throws Exception {
        int databaseSizeBeforeCreate = vectorRepository.findAll().size();

        // Create the Vector
        restVectorMockMvc.perform(post("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vector)))
            .andExpect(status().isCreated());

        // Validate the Vector in the database
        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeCreate + 1);
        Vector testVector = vectorList.get(vectorList.size() - 1);
        assertThat(testVector.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testVector.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testVector.getOriginjson()).isEqualTo(DEFAULT_ORIGINJSON);
        assertThat(testVector.getDestinationjson()).isEqualTo(DEFAULT_DESTINATIONJSON);
        assertThat(testVector.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testVector.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testVector.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testVector.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testVector.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Vector in Elasticsearch
        Vector vectorEs = vectorSearchRepository.findOne(testVector.getId());
        assertThat(vectorEs).isEqualToComparingFieldByField(testVector);
    }

    @Test
    @Transactional
    public void createVectorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = vectorRepository.findAll().size();

        // Create the Vector with an existing ID
        vector.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVectorMockMvc.perform(post("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vector)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = vectorRepository.findAll().size();
        // set the field null
        vector.setName(null);

        // Create the Vector, which fails.

        restVectorMockMvc.perform(post("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vector)))
            .andExpect(status().isBadRequest());

        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = vectorRepository.findAll().size();
        // set the field null
        vector.setNameshort(null);

        // Create the Vector, which fails.

        restVectorMockMvc.perform(post("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vector)))
            .andExpect(status().isBadRequest());

        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = vectorRepository.findAll().size();
        // set the field null
        vector.setStatus(null);

        // Create the Vector, which fails.

        restVectorMockMvc.perform(post("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vector)))
            .andExpect(status().isBadRequest());

        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = vectorRepository.findAll().size();
        // set the field null
        vector.setLastmodifiedby(null);

        // Create the Vector, which fails.

        restVectorMockMvc.perform(post("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vector)))
            .andExpect(status().isBadRequest());

        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = vectorRepository.findAll().size();
        // set the field null
        vector.setLastmodifieddatetime(null);

        // Create the Vector, which fails.

        restVectorMockMvc.perform(post("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vector)))
            .andExpect(status().isBadRequest());

        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = vectorRepository.findAll().size();
        // set the field null
        vector.setDomain(null);

        // Create the Vector, which fails.

        restVectorMockMvc.perform(post("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vector)))
            .andExpect(status().isBadRequest());

        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllVectors() throws Exception {
        // Initialize the database
        vectorRepository.saveAndFlush(vector);

        // Get all the vectorList
        restVectorMockMvc.perform(get("/api/vectors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vector.getId().intValue())))
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
    public void getVector() throws Exception {
        // Initialize the database
        vectorRepository.saveAndFlush(vector);

        // Get the vector
        restVectorMockMvc.perform(get("/api/vectors/{id}", vector.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(vector.getId().intValue()))
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
    public void getNonExistingVector() throws Exception {
        // Get the vector
        restVectorMockMvc.perform(get("/api/vectors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVector() throws Exception {
        // Initialize the database
        vectorRepository.saveAndFlush(vector);
        vectorSearchRepository.save(vector);
        int databaseSizeBeforeUpdate = vectorRepository.findAll().size();

        // Update the vector
        Vector updatedVector = vectorRepository.findOne(vector.getId());
        updatedVector
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .originjson(UPDATED_ORIGINJSON)
            .destinationjson(UPDATED_DESTINATIONJSON)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restVectorMockMvc.perform(put("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVector)))
            .andExpect(status().isOk());

        // Validate the Vector in the database
        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeUpdate);
        Vector testVector = vectorList.get(vectorList.size() - 1);
        assertThat(testVector.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testVector.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testVector.getOriginjson()).isEqualTo(UPDATED_ORIGINJSON);
        assertThat(testVector.getDestinationjson()).isEqualTo(UPDATED_DESTINATIONJSON);
        assertThat(testVector.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testVector.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testVector.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testVector.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testVector.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Vector in Elasticsearch
        Vector vectorEs = vectorSearchRepository.findOne(testVector.getId());
        assertThat(vectorEs).isEqualToComparingFieldByField(testVector);
    }

    @Test
    @Transactional
    public void updateNonExistingVector() throws Exception {
        int databaseSizeBeforeUpdate = vectorRepository.findAll().size();

        // Create the Vector

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restVectorMockMvc.perform(put("/api/vectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vector)))
            .andExpect(status().isCreated());

        // Validate the Vector in the database
        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteVector() throws Exception {
        // Initialize the database
        vectorRepository.saveAndFlush(vector);
        vectorSearchRepository.save(vector);
        int databaseSizeBeforeDelete = vectorRepository.findAll().size();

        // Get the vector
        restVectorMockMvc.perform(delete("/api/vectors/{id}", vector.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean vectorExistsInEs = vectorSearchRepository.exists(vector.getId());
        assertThat(vectorExistsInEs).isFalse();

        // Validate the database is empty
        List<Vector> vectorList = vectorRepository.findAll();
        assertThat(vectorList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchVector() throws Exception {
        // Initialize the database
        vectorRepository.saveAndFlush(vector);
        vectorSearchRepository.save(vector);

        // Search the vector
        restVectorMockMvc.perform(get("/api/_search/vectors?query=id:" + vector.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vector.getId().intValue())))
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
        TestUtil.equalsVerifier(Vector.class);
        Vector vector1 = new Vector();
        vector1.setId(1L);
        Vector vector2 = new Vector();
        vector2.setId(vector1.getId());
        assertThat(vector1).isEqualTo(vector2);
        vector2.setId(2L);
        assertThat(vector1).isNotEqualTo(vector2);
        vector1.setId(null);
        assertThat(vector1).isNotEqualTo(vector2);
    }
}
