package com.innvo.repository;

import com.innvo.domain.Pathwayclass;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Pathwayclass entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PathwayclassRepository extends JpaRepository<Pathwayclass,Long> {
    
}
