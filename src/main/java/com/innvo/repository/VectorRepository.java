package com.innvo.repository;

import com.innvo.domain.Vector;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Vector entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VectorRepository extends JpaRepository<Vector,Long> {
    
}
