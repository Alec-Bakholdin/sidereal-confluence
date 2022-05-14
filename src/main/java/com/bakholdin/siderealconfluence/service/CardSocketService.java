package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ConverterCard;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CardSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void notifyClientOfUpdatedCard(ResearchTeam card) {
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_CARD, card);
    }

    public void notifyClientOfUpdatedCard(Colony card) {
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_CARD, card);
    }

    public void notifyClientOfUpdatedCard(ConverterCard card) {
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_CARD, card);
    }
}
