package com.innvo.repository;

import com.innvo.domain.Pathway;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Pathway entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PathwayRepository extends JpaRepository<Pathway,Long> {
    
}
