package com.bakholdin.siderealconfluence.data.cardActions;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.model.cards.CardType;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CardActionService {
    private final CardService cardService;

    public String upgradeResearchTeam(String cardId) {
        if (cardService.contains(cardId)) {
            throw new IllegalArgumentException("Card with id " + cardId + " does not exist");
        }
        Card card = cardService.get(cardId);
        if (card.getType() != CardType.ResearchTeam) {
            throw new IllegalArgumentException("Card with id " + cardId + " is not a research team");
        }
        ResearchTeam researchTeam = (ResearchTeam) card;
        researchTeam.setResearched(true);
        return researchTeam.getResultingTechnology();
    }
}
