package com.bakholdin.siderealconfluence.init;

import com.bakholdin.siderealconfluence.entity.Card;
import com.bakholdin.siderealconfluence.repository.CardRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CardInit {
    private static final TypeReference<List<Card>> CARD_TYPE_REFERENCE = new TypeReference<>() {
    };
    private final CardRepository cardRepository;

    @Value("classpath:game_data/cards/converterCards1.json")
    private Resource converterCardsResource1;
    @Value("classpath:game_data/cards/converterCards2.json")
    private Resource converterCardsResource2;
    @Value("classpath:game_data/cards/colonies.json")
    private Resource coloniesResource;
    @Value("classpath:game_data/cards/researchTeams.json")
    private Resource researchTeamsResource;

    @PostConstruct
    public void init() {
        List<Card> cardList = InitUtils.readListFromResource(converterCardsResource1, CARD_TYPE_REFERENCE);
        cardList.addAll(InitUtils.readListFromResource(converterCardsResource2, CARD_TYPE_REFERENCE));
        cardList.addAll(InitUtils.readListFromResource(coloniesResource, CARD_TYPE_REFERENCE));
        cardList.addAll(InitUtils.readListFromResource(researchTeamsResource, CARD_TYPE_REFERENCE));

        Set<String> presentCards = cardRepository.findAll().stream()
                .map(Card::getId)
                .collect(Collectors.toSet());
        cardRepository.saveAll(cardList.stream()
                .filter(card -> !presentCards.contains(card.getId()))
                .collect(Collectors.toSet()));
    }
}
