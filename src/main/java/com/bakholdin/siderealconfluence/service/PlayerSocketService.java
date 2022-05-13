package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.service.model.TransferCardServerMessage;
import com.bakholdin.siderealconfluence.service.model.UpdatePlayerResourcesServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class PlayerSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;


    public void notifyClientOfUpdatedResources(Player player) {
        UpdatePlayerResourcesServerMessage msg = UpdatePlayerResourcesServerMessage.builder()
                .resources(player.getResources())
                .playerId(player.getId().toString())
                .build();
        log.info("Sending update player resources to client: {}", msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_PLAYER_UPDATED_RESOURCES, msg);
    }

    public void notifyClientOfCardTransfer(Player currentOwner, Player newPlayer, String cardId) {
        TransferCardServerMessage msg = TransferCardServerMessage.builder()
                .currentOwnerPlayerId(currentOwner.getId().toString())
                .newOwnerPlayerId(newPlayer.getId().toString())
                .cardId(cardId)
                .build();
        log.info("Sending transfer card notice to client: {}", msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_TRANSFER_CARD, msg);
    }
}
