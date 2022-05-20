package com.bakholdin.siderealconfluence.old_service;

import com.bakholdin.siderealconfluence.old_model.GameState1;
import com.bakholdin.siderealconfluence.old_model.PlayerBid1;
import com.bakholdin.siderealconfluence.old_service.model.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.old_service.model.UpdateGameStateServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class GameStateSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void updateGameState(UpdateGameStateServerMessage message) {
        log.info(message);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_GAME_STATE, message);
    }

    public void updateGameStateWholesale(GameState1 gameState) {
        log.info("Wholesale gameState update");
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_GAME_STATE_WHOLESALE, gameState);
    }

    public void revealPlayerBids(List<PlayerBid1> playerBids) {
        log.info(playerBids.toString());
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_REVEAL_BIDS, playerBids);
    }
}
