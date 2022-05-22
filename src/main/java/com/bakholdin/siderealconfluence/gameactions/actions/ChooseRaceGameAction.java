package com.bakholdin.siderealconfluence.gameactions.actions;

import com.bakholdin.siderealconfluence.dto.UpdatePlayerDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Player;
import com.bakholdin.siderealconfluence.entity.Race;
import com.bakholdin.siderealconfluence.enums.GameState;
import com.bakholdin.siderealconfluence.exceptions.UserException;
import com.bakholdin.siderealconfluence.gameactions.GameAction;
import com.bakholdin.siderealconfluence.gameactions.GameActionDto;
import com.bakholdin.siderealconfluence.gameactions.GameActionOrder;
import com.bakholdin.siderealconfluence.gameactions.GameActionUpdateContainer;
import com.bakholdin.siderealconfluence.gameactions.dto.ChooseRaceDto;
import com.bakholdin.siderealconfluence.mapper.RaceMapper;
import com.bakholdin.siderealconfluence.repository.PlayerRepository;
import com.bakholdin.siderealconfluence.repository.RaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(GameActionOrder.CHOOSE_RACE)
@RequiredArgsConstructor
public class ChooseRaceGameAction implements GameAction {
    private final PlayerRepository playerRepository;
    private final RaceRepository raceRepository;
    private final RaceMapper raceMapper;

    @Override
    public void validate(UserDto userDto, GameActionDto gameActionDto) {
        ChooseRaceDto chooseRaceDto = (ChooseRaceDto) gameActionDto;
        raceRepository.getByRaceType(chooseRaceDto.getRaceType());
        Player player = playerRepository.getByUsername(userDto.getUsername());
        if (player.getGame().getState() != GameState.Lobby) {
            throw new UserException("Race is only selectable in Lobby");
        }
    }

    @Override
    public boolean supports(GameActionDto gameActionDto) {
        return gameActionDto instanceof ChooseRaceDto;
    }

    @Override
    public void resolve(UserDto userDto, GameActionDto gameActionDto, GameActionUpdateContainer gameActionUpdateContainer) {
        ChooseRaceDto chooseRaceDto = (ChooseRaceDto) gameActionDto;
        Race race = raceRepository.getByRaceType(chooseRaceDto.getRaceType());
        Player player = playerRepository.getByUsername(userDto.getUsername());
        UpdatePlayerDto.Builder playerUpdater = gameActionUpdateContainer.getUpdatePlayerBuilder(userDto);

        player.setRace(race);
        playerRepository.save(player);
        playerUpdater.race(raceMapper.toDto(race));
    }
}
