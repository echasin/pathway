package com.innvo.repository;

import com.innvo.domain.Pathwayrecordtype;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Pathwayrecordtype entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PathwayrecordtypeRepository extends JpaRepository<Pathwayrecordtype,Long> {
    
}
