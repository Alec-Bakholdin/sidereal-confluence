package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.ActiveCardDto;
import com.bakholdin.siderealconfluence.dto.CardDto;
import com.bakholdin.siderealconfluence.entity.ActiveCard;
import com.bakholdin.siderealconfluence.entity.Card;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CardMapper {

    CardDto toDto(Card card);

    Set<CardDto> toDto(Collection<Card> cards);

    ActiveCardDto toActiveCardDto(ActiveCard activeCard);

    Set<ActiveCardDto> toActiveCardDto(Collection<ActiveCard> cards);

    default ActiveCard toActiveCard(Card card) {
        return ActiveCard.builder()
                .card(card)
                .upgraded(false)
                .build();
    }
}
