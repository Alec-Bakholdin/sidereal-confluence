package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.service.model.AcquireCardServerMessage;
import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.service.model.RemoveActiveCardServerMessage;
import com.bakholdin.siderealconfluence.service.model.TransferCardServerMessage;
import com.bakholdin.siderealconfluence.service.model.UpdatePlayerReadyStatusServerMessage;
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
                .donations(player.getDonations())
                .playerId(player.getId().toString())
                .build();
        log.info(msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_PLAYER_UPDATED_RESOURCES, msg);
    }

    public void notifyClientOfCardTransfer(Player currentOwner, Player newPlayer, String cardId) {
        TransferCardServerMessage msg = TransferCardServerMessage.builder()
                .currentOwnerPlayerId(currentOwner.getId().toString())
                .newOwnerPlayerId(newPlayer.getId().toString())
                .cardId(cardId)
                .build();
        log.info(msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_TRANSFER_CARD, msg);
    }

    public void notifyClientOfAcquiredCard(Player player, Card card) {
        AcquireCardServerMessage msg = AcquireCardServerMessage.builder()
                .playerId(player.getId())
                .cardId(card.getId())
                .build();
        log.info(msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_ACQUIRED_CARD, msg);
    }

    public void notifyClientOfRemovedActiveCard(Player player, Card card) {
        RemoveActiveCardServerMessage msg = RemoveActiveCardServerMessage.builder()
                .playerId(player.getId())
                .cardId(card.getId())
                .build();
        log.info(msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_REMOVED_ACTIVE_CARD, msg);
    }

    public void notifyClientOfUpdatedReadyStatus(Player player) {
        UpdatePlayerReadyStatusServerMessage msg = UpdatePlayerReadyStatusServerMessage.builder()
                .playerId(player.getId())
                .ready(player.isReady())
                .build();
        log.info(msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_READY_STATUS, msg);
    }
}
