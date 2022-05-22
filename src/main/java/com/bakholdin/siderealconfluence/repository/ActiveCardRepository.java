package com.bakholdin.siderealconfluence.repository;

import com.bakholdin.siderealconfluence.entity.ActiveCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActiveCardRepository extends JpaRepository<ActiveCard, UUID> {
}
