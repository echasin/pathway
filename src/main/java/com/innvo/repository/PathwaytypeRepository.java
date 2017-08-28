package com.innvo.repository;

import com.innvo.domain.Pathwaytype;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Pathwaytype entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PathwaytypeRepository extends JpaRepository<Pathwaytype,Long> {
    
}
