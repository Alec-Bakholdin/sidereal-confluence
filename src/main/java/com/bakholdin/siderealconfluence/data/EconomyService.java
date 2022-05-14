package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.controllers.model.EconomyAction;
import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.model.Converter;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.Resources;
import com.bakholdin.siderealconfluence.model.cards.Card;
import com.bakholdin.siderealconfluence.service.PlayerSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class EconomyService {
    private final PlayerService playerService;
    private final PlayerSocketService playerSocketService;
    private final CardService cardService;
    private final Map<UUID, List<EconomyAction>> economyActionMap = new HashMap<>();

    public void updateEconomyActions(UUID playerId, List<EconomyAction> economyActions) {
        economyActionMap.merge(playerId, economyActions, (oldValue, newValue) -> newValue);
    }

    public void resolveEconomyStep() {
        log.info("Resolving economy step");
        economyActionMap.forEach((playerId, economyActions) -> {
            Player player = playerService.get(playerId);
            Resources econInput = new Resources();
            Resources econOutput = new Resources();

            economyActions.forEach(economyAction -> {
                if (player == null || !player.getCards().contains(economyAction.getCardId())) {
                    return;
                }
                Card card = cardService.get(economyAction.getCardId());
                Converter converter = card.activeConverters().get(economyAction.getConverterIndex());
                if (converter.getPhase() == Phase.Economy) {
                    econInput.add(converter.getInput());
                    econOutput.add(converter.getOutput());
                }
            });


            if (econInput.resourceTotal() > 0 || econOutput.resourceTotal() > 0) {
                player.getResources().subtract(econInput);
                player.getResources().add(econOutput);
                playerSocketService.notifyClientOfUpdatedResources(player);
                log.info("Economy step: {} pays {} resources and receives {}", player.getName(), econInput.resourceTotal(), econOutput.resourceTotal());
            }
        });
    }
}
