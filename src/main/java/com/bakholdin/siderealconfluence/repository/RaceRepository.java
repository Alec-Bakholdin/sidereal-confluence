package com.bakholdin.siderealconfluence.repository;

import com.bakholdin.siderealconfluence.entity.Race;
import com.bakholdin.siderealconfluence.enums.RaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceRepository extends JpaRepository<Race, RaceType> {
}
