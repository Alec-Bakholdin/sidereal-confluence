package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.model.IncomingSocketTopics;
import com.bakholdin.siderealconfluence.controllers.model.TransferCardClientMessage;
import com.bakholdin.siderealconfluence.controllers.model.TransferCardServerMessage;
import com.bakholdin.siderealconfluence.controllers.model.UpdateEconomyActionsClientMessage;
import com.bakholdin.siderealconfluence.controllers.model.UpdatePlayerResourcesClientMessage;
import com.bakholdin.siderealconfluence.data.EconomyService;
import com.bakholdin.siderealconfluence.data.PlayerService;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.service.PlayerSocketService;
import com.bakholdin.siderealconfluence.service.model.OutgoingSocketTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerSocketService playerSocketService;
    private final EconomyService economyService;

    @MessageMapping(IncomingSocketTopics.APP_UPDATE_PLAYER_RESOURCES)
    public void updatePlayerResources(UpdatePlayerResourcesClientMessage payload) {
        Player player = playerService.get(UUID.fromString(payload.getPlayerId()));
        if (player == null) {
            throw new IllegalArgumentException("Player not found");
        }
        player.setResources(payload.getResources());
        playerSocketService.sendUpdatePlayerResourcesToClient(player);
    }

    @MessageMapping(IncomingSocketTopics.APP_UPDATE_ECONOMY_ACTIONS)
    public void updateEconomyActions(UpdateEconomyActionsClientMessage payload) {
        if (!playerService.contains(payload.getPlayerId())) {
            throw new IllegalArgumentException("Player not found");
        }
        Player player = playerService.get(payload.getPlayerId());
        log.info("Updating {} economy actions for {}", payload.getActions().size(), player.getName());
        economyService.updateEconomyActions(UUID.fromString(payload.getPlayerId()), payload.getActions());
    }

    @MessageMapping(IncomingSocketTopics.APP_TRANSFER_CARD)
    @SendTo(OutgoingSocketTopics.TOPIC_TRANSFER_CARD)
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
