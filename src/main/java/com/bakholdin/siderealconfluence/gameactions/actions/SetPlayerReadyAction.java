package com.bakholdin.siderealconfluence.gameactions.actions;

import com.bakholdin.siderealconfluence.dto.UpdatePlayerDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Player;
import com.bakholdin.siderealconfluence.enums.GameState;
import com.bakholdin.siderealconfluence.exceptions.UserException;
import com.bakholdin.siderealconfluence.gameactions.GameAction;
import com.bakholdin.siderealconfluence.gameactions.GameActionDto;
import com.bakholdin.siderealconfluence.gameactions.GameActionOrder;
import com.bakholdin.siderealconfluence.gameactions.GameActionUpdateContainer;
import com.bakholdin.siderealconfluence.gameactions.dto.PlayerReadyDto;
import com.bakholdin.siderealconfluence.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(GameActionOrder.PLAYER_READY)
@Component
@RequiredArgsConstructor
public class SetPlayerReadyAction implements GameAction {
    private final PlayerRepository playerRepository;

    @Override
    public void validate(UserDto userDto, GameActionDto gameActionDto) {
        Player player = playerRepository.getByUsername(userDto.getUsername());
        if (player.getGame().getState() == GameState.Lobby && player.getRace() == null) {
            throw new UserException("You must select a race before readying up");
        }
    }

    @Override
    public void resolve(UserDto userDto, GameActionDto gameActionDto, GameActionUpdateContainer gameActionUpdateContainer) {
        PlayerReadyDto playerReadyDto = (PlayerReadyDto) gameActionDto;
        Player player = playerRepository.getByUsername(userDto.getUsername());
        UpdatePlayerDto.Builder updatePlayerBuilder = gameActionUpdateContainer.getUpdatePlayerBuilder(userDto);

        player.setReady(playerReadyDto.isReady());
        playerRepository.save(player);
        updatePlayerBuilder.ready(playerReadyDto.isReady());
    }

    @Override
    public boolean supports(GameActionDto gameActionDto) {
        return gameActionDto instanceof PlayerReadyDto;
    }
}
