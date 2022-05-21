package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.socket.IncomingSocketTopics;
import com.bakholdin.siderealconfluence.controllers.socket.SocketUtil;
import com.bakholdin.siderealconfluence.gameactions.GameActionChain;
import com.bakholdin.siderealconfluence.gameactions.dto.ChooseRaceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PlayerController {
    private final GameActionChain gameActionChain;

    @MessageMapping(IncomingSocketTopics.APP_CHOOSE_RACE)
    public void chooseRace(
            @Payload ChooseRaceDto chooseRaceDto,
            @AuthenticationPrincipal Principal principal) {
        gameActionChain.resolveAction(SocketUtil.getUserDto(principal), chooseRaceDto);
    }
}
