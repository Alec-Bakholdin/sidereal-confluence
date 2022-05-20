package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.data.cards.CardService;
import com.bakholdin.siderealconfluence.old_model.Converter1;
import com.bakholdin.siderealconfluence.old_model.EconomyAction;
import com.bakholdin.siderealconfluence.old_model.Phase1;
import com.bakholdin.siderealconfluence.old_model.Player1;
import com.bakholdin.siderealconfluence.old_model.Resources1;
import com.bakholdin.siderealconfluence.old_model.cards.Card1;
import com.bakholdin.siderealconfluence.old_model.cards.CardType1;
import com.bakholdin.siderealconfluence.old_model.cards.Colony1;
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
    private final CardService cardService;
    private final Map<UUID, List<EconomyAction>> economyActionMap = new HashMap<>();

    public void updateEconomyActions(UUID playerId, List<EconomyAction> economyActions) {
        economyActionMap.merge(playerId, economyActions, (oldValue, newValue) -> newValue);
    }

    public void resolveEconomyStep() {
        log.info("Resolving economy step");
        economyActionMap.forEach((playerId, economyActions) -> {
            if (!playerService.contains(playerId)) {
                return;
            }
            Player1 player = playerService.get(playerId);
            Resources1 econInput = new Resources1();
            Resources1 econOutput = new Resources1();
            Resources1 econDonations = new Resources1();

            for (EconomyAction economyAction : economyActions) {
                addEconActionToTotal(playerId, econInput, econOutput, econDonations, economyAction);
            }

            if (econInput.resourceTotal() > 0 || econOutput.resourceTotal() > 0) {
                playerService.updatePlayerResources(playerId, econInput, econOutput, econDonations);
                log.info("Economy step: {} pays {} resources and receives {}", player.getName(), econInput.resourceTotal(), econOutput.resourceTotal());
            }
        });
    }

    private void addEconActionToTotal(UUID playerId, Resources1 econInput, Resources1 econOutput, Resources1 econDonations, EconomyAction economyAction) {
        if (!playerService.hasCardActive(playerId, economyAction.getCardId())) {
            return;
        }

        Card1 card1 = cardService.get(economyAction.getCardId());
        Converter1 converter1 = card1.activeConverters().get(economyAction.getConverterIndex());
        if (converter1.getPhase() == Phase1.Economy) {
            econInput.add(converter1.getInput());
            econOutput.add(converter1.getOutput());
            econDonations.add(converter1.getDonations());
        }
        if (card1.getType() == CardType1.Colony && ((Colony1) card1).isDoubledWithCaylion()) {
            econInput.add(converter1.getInput());
            econOutput.add(converter1.getOutput());
            econDonations.add(converter1.getDonations());
        }
    }
}
