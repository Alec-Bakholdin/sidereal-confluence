package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.model.Player1;
import com.bakholdin.siderealconfluence.model.cards.Card1;
import com.bakholdin.siderealconfluence.service.model.AcquireCardServerMessage;
import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.service.model.RemoveActiveCardServerMessage;
import com.bakholdin.siderealconfluence.service.model.UpdatePlayerReadyStatusServerMessage;
import com.bakholdin.siderealconfluence.service.model.UpdatePlayerServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class PlayerSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;


    public void notifyClientOfUpdatedResources(Player1 player) {
        UpdatePlayerServerMessage msg = UpdatePlayerServerMessage.builder()
                .playerId(player.getId())
                .resources(player.getResources())
                .donations(player.getDonations())
                .build();
        log.info(msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_PLAYER, msg);
    }

    public void notifyClientOfCardTransfer(Player1 currentOwner, Player1 newPlayer, String cardId) {
        UpdatePlayerServerMessage currentOwnerMsg = UpdatePlayerServerMessage.builder()
                .playerId(currentOwner.getId())
                .cards(currentOwner.cardIds())
                .inactiveCards(currentOwner.inactiveCardIds())
                .build();
        log.info(currentOwnerMsg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_PLAYER, currentOwnerMsg);
        UpdatePlayerServerMessage newPlayerMsg = UpdatePlayerServerMessage.builder()
                .playerId(newPlayer.getId())
                .cards(newPlayer.cardIds())
                .inactiveCards(newPlayer.inactiveCardIds())
                .build();
        log.info(newPlayerMsg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_PLAYER, newPlayerMsg);
    }

    public void notifyClientOfAcquiredCard(Player1 player, Card1 card1) {
        AcquireCardServerMessage msg = AcquireCardServerMessage.builder()
                .playerId(player.getId())
                .cardId(card1.getId())
                .build();
        log.info(msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_ACQUIRED_CARD, msg);
    }

    public void notifyClientOfRemovedActiveCard(Player1 player, Card1 card1) {
        RemoveActiveCardServerMessage msg = RemoveActiveCardServerMessage.builder()
                .playerId(player.getId())
                .cardId(card1.getId())
                .build();
        log.info(msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_REMOVED_ACTIVE_CARD, msg);
    }

    public void notifyClientOfUpdatedReadyStatus(Player1 player) {
        UpdatePlayerReadyStatusServerMessage msg = UpdatePlayerReadyStatusServerMessage.builder()
                .playerId(player.getId())
                .ready(player.isReady())
                .build();
        log.info(msg);
        simpMessagingTemplate.convertAndSend(OutgoingSocketTopics.TOPIC_UPDATE_READY_STATUS, msg);
    }
}
