package com.bakholdin.siderealconfluence.repository;

import com.bakholdin.siderealconfluence.repository.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {
}
