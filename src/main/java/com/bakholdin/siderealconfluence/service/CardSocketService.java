package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.model.cards.Card1;
import com.bakholdin.siderealconfluence.model.cards.Colony1;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard1;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam1;
import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class CardSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void notifyClientOfUpdatedCard(ResearchTeam1 card) {
        log.info("Updating card: {}", card);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_CARD, card);
    }

    public void notifyClientOfUpdatedCard(Colony1 card) {
        log.info("Updating card: {}", card);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_CARD, card);
    }

    public void notifyClientOfUpdatedCard(ConverterCard1 card) {
        log.info("Updating card: {}", card);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_CARD, card);
    }

    public void updateAllCards(Map<String, Card1> cards) {
        log.info("Updating all cards: {} cards", cards.size());
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_ALL_CARDS, cards);
    }
}
