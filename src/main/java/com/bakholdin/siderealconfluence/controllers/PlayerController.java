package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.model.SocketTopics;
import com.bakholdin.siderealconfluence.controllers.model.TransferCardClientMessage;
import com.bakholdin.siderealconfluence.controllers.model.TransferCardServerMessage;
import com.bakholdin.siderealconfluence.controllers.model.UpdatePlayerResourcesClientMessage;
import com.bakholdin.siderealconfluence.controllers.model.UpdatePlayerResourcesServerMessage;
import com.bakholdin.siderealconfluence.data.PlayerService;
import com.bakholdin.siderealconfluence.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @MessageMapping(SocketTopics.APP_UPDATE_PLAYER_RESOURCES)
    @SendTo(SocketTopics.TOPIC_PLAYER_UPDATED_RESOURCES)
    public UpdatePlayerResourcesServerMessage updatePlayerResources(UpdatePlayerResourcesClientMessage payload) {
        Player player = playerService.get(UUID.fromString(payload.getPlayerId()));
        if (player == null) {
            throw new IllegalArgumentException("Player not found");
        }
        player.setResources(payload.getResources());
        return UpdatePlayerResourcesServerMessage.builder()
                .resources(payload.getResources())
                .playerId(payload.getPlayerId())
                .build();
    }

    @MessageMapping(SocketTopics.APP_TRANSFER_CARD)
    @SendTo(SocketTopics.TOPIC_TRANSFER_CARD)
    public TransferCardServerMessage transferCard(TransferCardClientMessage payload) {
        Player currentOwner = playerService.get(UUID.fromString(payload.getCurrentOwnerPlayerId()));
        Player newOwner = playerService.get(UUID.fromString(payload.getNewOwnerPlayerId()));
        if (currentOwner == null || newOwner == null) {
            throw new IllegalArgumentException("Player not found");
        }
        if (!currentOwner.getCards().contains(payload.getCardId())) {
            throw new IllegalArgumentException("Card not found in owner's possession");
        }
        currentOwner.getCards().remove(payload.getCardId());
        newOwner.getCards().add(payload.getCardId());
        return TransferCardServerMessage.builder()
                .currentOwnerPlayerId(payload.getCurrentOwnerPlayerId())
                .newOwnerPlayerId(payload.getNewOwnerPlayerId())
                .cardId(payload.getCardId())
                .build();
    }
}
