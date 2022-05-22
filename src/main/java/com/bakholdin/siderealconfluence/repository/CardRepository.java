package com.bakholdin.siderealconfluence.repository;

import com.bakholdin.siderealconfluence.entity.Card;
import com.bakholdin.siderealconfluence.enums.CardType;
import com.bakholdin.siderealconfluence.enums.RaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {
    Set<Card> findByCardType(CardType cardType);

    @Query("select c from Card c where c.cardType = 'ConverterCard' and c.converterCard.race = ?1")
    Set<Card> findAllConverterCardsForRace(RaceType raceType);

    default Set<Card> findStartingConverterCards(RaceType raceType) {
        return findAllConverterCardsForRace(raceType).stream()
                .filter(card -> card.getConverterCard().getStarting())
                .collect(Collectors.toSet());
    }
}
