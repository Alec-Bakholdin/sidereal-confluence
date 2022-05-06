package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CardService {

    @Value(value = "classpath:game_data/cards/converterCards.json")
    private Resource converterCardsResource;
    @Value(value = "classpath:game_data/cards/researchTeams.json")
    private Resource researchTeamsResource;
    @Value(value = "classpath:game_data/cards/colonies.json")
    private Resource coloniesResource;

    private Map<String, Card> allCards = new HashMap<>();
    private List<Colony> availableColonies = new LinkedList<>();
    private List<ResearchTeam> availableResearchTeams = new LinkedList<>();


    public Map<String, Card> resetCards() {
        allCards = new HashMap<>();
        loadConverterCards();
        loadResearchTeams();
        loadColonies();

        return allCards;
    }

    public Map<String, Card> getCurrentGameCards() {
        if (allCards == null) {
            return resetCards();
        }
        return allCards;
    }

    public Card get(String id) {
        return getCurrentGameCards().get(id);
    }

    public Colony extractRandomColony() {
        if (availableColonies.isEmpty()) {
            throw new RuntimeException("No more colonies available");
        }
        return availableColonies.remove(0);
    }

    public ResearchTeam extractRandomResearchTeam() {
        if (availableResearchTeams.isEmpty()) {
            throw new RuntimeException("No more research teams available");
        }
        return availableResearchTeams.remove(0);
    }


    private void loadColonies() {
        availableColonies = ResourceUtils.loadListFromResource(coloniesResource);
        Map<String, Colony> colonyMap = availableColonies.stream().collect(Collectors.toMap(Card::getId, card -> card));
        allCards.putAll(colonyMap);
        Collections.shuffle(availableColonies);
        log.info("Loaded {} colonies", availableColonies.size());
    }

    private void loadResearchTeams() {
        availableResearchTeams = ResourceUtils.loadListFromResource(researchTeamsResource);
        Map<String, ResearchTeam> researchTeamMap = availableResearchTeams.stream().collect(Collectors.toMap(Card::getId, card -> card));
        allCards.putAll(researchTeamMap);

        // order randomly within each era
        Collections.shuffle(availableResearchTeams);
        availableResearchTeams.sort(Comparator.comparingInt(ResearchTeam::getEra));
        log.info("Loaded {} research teams", availableResearchTeams.size());
    }

    private void loadConverterCards() {
        List<ConverterCard> converterCardList = ResourceUtils.loadListFromResource(converterCardsResource);
        Map<String, ConverterCard> converterCardMap = converterCardList.stream().collect(Collectors.toMap(Card::getId, card -> card));
        allCards.putAll(converterCardMap);
        log.info("Loaded {} converter cards", converterCardList.size());
    }


}
