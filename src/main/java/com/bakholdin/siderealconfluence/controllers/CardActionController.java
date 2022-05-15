package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.model.FlipResearchTeamClientMessage;
import com.bakholdin.siderealconfluence.controllers.model.IncomingSocketTopics;
import com.bakholdin.siderealconfluence.controllers.model.UpgradeColonyClientMessage;
import com.bakholdin.siderealconfluence.controllers.model.UpgradeConverterCard;
import com.bakholdin.siderealconfluence.data.cardActions.CardActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class CardActionController {
    private final CardActionService cardActionService;

    @MessageMapping(IncomingSocketTopics.APP_FLIP_RESEARCH_TEAM)
    private void flipResearchTeam(FlipResearchTeamClientMessage payload) {
        cardActionService.upgradeResearchTeam(payload.getPlayerId(), payload.getCardId(), payload.getCost());
    }

    @MessageMapping(IncomingSocketTopics.APP_UPGRADE_COLONY)
    private void upgradeColony(UpgradeColonyClientMessage payload) {
        cardActionService.upgradeColony(payload.getPlayerId(), payload.getCardId());
    }

    @MessageMapping(IncomingSocketTopics.APP_UPGRADE_CONVERTER_CARD)
    private void upgradeConverterCard(UpgradeConverterCard payload) {
        cardActionService.upgradeConverterCard(payload.getPlayerId(), payload.getCardId(), payload.getTechnology());
    }
}
