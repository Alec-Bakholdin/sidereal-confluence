package com.bakholdin.siderealconfluence.gameactions.actions;

import com.bakholdin.siderealconfluence.dto.UpdatePlayerDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Player;
import com.bakholdin.siderealconfluence.entity.Race;
import com.bakholdin.siderealconfluence.enums.RaceType;
import com.bakholdin.siderealconfluence.gameactions.GameAction;
import com.bakholdin.siderealconfluence.gameactions.GameActionDto;
import com.bakholdin.siderealconfluence.gameactions.GameActionUpdateContainer;
import com.bakholdin.siderealconfluence.gameactions.dto.ChooseRaceDto;
import com.bakholdin.siderealconfluence.mapper.RaceMapper;
import com.bakholdin.siderealconfluence.repository.PlayerRepository;
import com.bakholdin.siderealconfluence.repository.RaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChooseRaceGameAction implements GameAction {
    private final PlayerRepository playerRepository;
    private final RaceRepository raceRepository;
    private final RaceMapper raceMapper;

    @Override
    public void validate(UserDto userDto, GameActionDto gameActionDto) {
        ChooseRaceDto chooseRaceDto = (ChooseRaceDto) gameActionDto;
        getRace(chooseRaceDto.getRaceType());
        getActivePlayer(userDto.getUsername());
    }

    @Override
    public boolean supports(GameActionDto gameActionDto) {
        return gameActionDto instanceof ChooseRaceDto;
    }

    @Override
    public void resolve(UserDto userDto, GameActionDto gameActionDto, GameActionUpdateContainer gameActionUpdateContainer) {
        ChooseRaceDto chooseRaceDto = (ChooseRaceDto) gameActionDto;
        Race race = getRace(chooseRaceDto.getRaceType());
        Player player = getActivePlayer(userDto.getUsername());
        UpdatePlayerDto.Builder playerUpdater = gameActionUpdateContainer.fetchPlayerUpdater(userDto);

        player.setRace(race);
        playerRepository.save(player);
        playerUpdater.race(raceMapper.toDto(race));
    }

    private Race getRace(RaceType raceType) {
        return raceRepository.findById(raceType)
                .orElseThrow(() -> new RuntimeException(String.format("Race %s does not exist", raceType)));
    }

    private Player getActivePlayer(String username) {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(String.format("User %s is not in game", username)));
    }
}
