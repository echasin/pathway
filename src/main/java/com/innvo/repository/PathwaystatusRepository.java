package com.innvo.repository;

import com.innvo.domain.Pathwaystatus;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Pathwaystatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PathwaystatusRepository extends JpaRepository<Pathwaystatus,Long> {
    
}
