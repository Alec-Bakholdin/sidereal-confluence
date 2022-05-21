package com.bakholdin.siderealconfluence.gameactions;

import com.bakholdin.siderealconfluence.controllers.socket.OutgoingSocketTopics;
import com.bakholdin.siderealconfluence.dto.UpdateGameDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GameActionChain {
    private final List<GameAction> gameActions;
    private final UserMapper userMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void resolveAction(UserDto userDto, GameActionDto gameActionDto) {
        List<GameAction> supportingActions = getAndValidateSupportingActions(userDto, gameActionDto);
        GameActionUpdateContainer gameActionUpdateContainer = resolveGameActions(userDto, gameActionDto, supportingActions);

        notifyOfGameUpdates(userDto, gameActionUpdateContainer);
        notifyOfPlayerUpdates(userDto, gameActionUpdateContainer);
    }

    private void notifyOfPlayerUpdates(UserDto userDto, GameActionUpdateContainer gameActionUpdateContainer) {
        String updatePlayerTopic = OutgoingSocketTopics.TOPIC_GAME_UPDATE_PLAYER(userDto.getGame().getId());
        gameActionUpdateContainer.buildUpdatePlayerDtos()
                .forEach(updatePlayerDto ->
                        simpMessagingTemplate.convertAndSend(updatePlayerTopic, updatePlayerDto)
                );
    }

    private void notifyOfGameUpdates(UserDto userDto, GameActionUpdateContainer gameActionUpdateContainer) {
        if (gameActionUpdateContainer.getUpdateGameDtoBuilder() != null) {
            String updateGameTopic = OutgoingSocketTopics.TOPIC_GAME_UPDATE_GAME(userDto.getGame().getId());
            UpdateGameDto updateGameDto = gameActionUpdateContainer.buildUpdateGameDto();
            simpMessagingTemplate.convertAndSend(updateGameTopic, updateGameDto);
        }
    }

    private GameActionUpdateContainer resolveGameActions(UserDto userDto, GameActionDto gameActionDto, List<GameAction> supportingActions) {
        // resolve updates
        GameActionUpdateContainer gameActionUpdateContainer = new GameActionUpdateContainer(userDto.getGame().getId(), userMapper);
        supportingActions.forEach(action -> action.resolve(userDto, gameActionDto, gameActionUpdateContainer));
        return gameActionUpdateContainer;
    }

    private List<GameAction> getAndValidateSupportingActions(UserDto userDto, GameActionDto gameActionDto) {
        List<GameAction> supportingActions = gameActions.stream()
                .filter(action -> action.supports(gameActionDto))
                .collect(Collectors.toList());
        supportingActions.forEach(action -> action.validate(userDto, gameActionDto));
        if (userDto.getGame() == null) {
            throw new RuntimeException("User is not in a game");
        }
        return supportingActions;
    }
}
