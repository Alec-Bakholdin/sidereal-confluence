package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.model.IncomingSocketTopics;
import com.bakholdin.siderealconfluence.controllers.model.SetPlayerBidsClientMessage;
import com.bakholdin.siderealconfluence.controllers.model.TransferCardClientMessage;
import com.bakholdin.siderealconfluence.controllers.model.UpdateEconomyActionsClientMessage;
import com.bakholdin.siderealconfluence.controllers.model.UpdatePlayerResourcesClientMessage;
import com.bakholdin.siderealconfluence.data.EconomyService;
import com.bakholdin.siderealconfluence.data.GameStateService;
import com.bakholdin.siderealconfluence.data.PlayerService;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.service.PlayerSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;
    private final GameStateService gameStateService;
    private final PlayerSocketService playerSocketService;
    private final EconomyService economyService;

    @MessageMapping(IncomingSocketTopics.APP_UPDATE_PLAYER_RESOURCES)
    public void updatePlayerResources(UpdatePlayerResourcesClientMessage payload) {
        Player player = playerService.get(UUID.fromString(payload.getPlayerId()));
        if (player == null) {
            throw new IllegalArgumentException("Player not found");
        }
        if (payload.isDonations()) {
            player.setDonations(payload.getResources());
        } else {
            player.setResources(payload.getResources());
        }
        playerSocketService.notifyClientOfUpdatedResources(player);
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
    public void transferCard(TransferCardClientMessage payload) {
        String currentOwner = payload.getCurrentOwnerPlayerId();
        String newOwner = payload.getNewOwnerPlayerId();
        String cardId = payload.getCardId();
        playerService.transferCard(currentOwner, newOwner, cardId);
    }

    @MessageMapping(IncomingSocketTopics.APP_SET_PLAYER_BIDS)
    public void setPlayerBids(SetPlayerBidsClientMessage payload) {
        if (gameStateService.getGameState().getPhase() != Phase.Confluence) {
            throw new IllegalArgumentException("Can't set bids in this phase");
        }
        playerService.setPlayerBid(payload.getPlayerId(), payload.getColonyBid(), payload.getResearchTeamBid());
    }
}
