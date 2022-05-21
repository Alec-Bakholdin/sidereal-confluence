package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.controllers.socket.IncomingSocketTopics;
import com.bakholdin.siderealconfluence.controllers.socket.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.controllers.socket.SocketUtil;
import com.bakholdin.siderealconfluence.dto.ChooseRaceDto;
import com.bakholdin.siderealconfluence.dto.UpdatePlayerDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Player;
import com.bakholdin.siderealconfluence.entity.Race;
import com.bakholdin.siderealconfluence.mapper.RaceMapper;
import com.bakholdin.siderealconfluence.mapper.UserMapper;
import com.bakholdin.siderealconfluence.repository.PlayerRepository;
import com.bakholdin.siderealconfluence.repository.RaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PlayerController {
    private final UserMapper userMapper;
    private final RaceMapper raceMapper;
    private final PlayerRepository playerRepository;
    private final RaceRepository raceRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(IncomingSocketTopics.APP_CHOOSE_RACE)
    public void chooseRace(
            @Payload ChooseRaceDto chooseRaceDto,
            @AuthenticationPrincipal Principal principal) {
        UserDto userDto = SocketUtil.getUserDto(principal);
        Player player = playerRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Active player not found"));
        Race race = raceRepository.findById(chooseRaceDto.getRaceType())
                .orElseThrow(() -> new RuntimeException(String.format("Race %s not found", chooseRaceDto.getRaceType())));
        player.setRace(race);
        playerRepository.save(player);
        UpdatePlayerDto updatePlayerDto = UpdatePlayerDto.builder()
                .user(userMapper.toUsernameOnly(userDto))
                .race(raceMapper.toDto(race))
                .build();
        simpMessagingTemplate.convertAndSend(
                OutgoingSocketTopics.TOPIC_GAME_UPDATE_PLAYER(player.getGame().getId()),
                updatePlayerDto
        );
    }
}
