package com.bakholdin.siderealconfluence.repository;

import com.bakholdin.siderealconfluence.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}
