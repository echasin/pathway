package com.innvo.repository;

import com.innvo.domain.Pathwaycategory;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Pathwaycategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PathwaycategoryRepository extends JpaRepository<Pathwaycategory,Long> {
    
}
