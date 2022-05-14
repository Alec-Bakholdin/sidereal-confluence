package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.service.model.UpdateGameStateServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class GameStateSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void updateGameState(UpdateGameStateServerMessage message) {
        log.info(message);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_GAME_STATE, message);
    }
}
