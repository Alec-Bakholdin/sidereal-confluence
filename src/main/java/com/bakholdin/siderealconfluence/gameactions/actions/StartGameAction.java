package com.bakholdin.siderealconfluence.gameactions.actions;

import com.bakholdin.siderealconfluence.dto.UpdateGameDto;
import com.bakholdin.siderealconfluence.dto.UpdatePlayerDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.ActiveCard;
import com.bakholdin.siderealconfluence.entity.Game;
import com.bakholdin.siderealconfluence.entity.Player;
import com.bakholdin.siderealconfluence.entity.Resources;
import com.bakholdin.siderealconfluence.enums.CardType;
import com.bakholdin.siderealconfluence.enums.GamePhase;
import com.bakholdin.siderealconfluence.enums.GameState;
import com.bakholdin.siderealconfluence.exceptions.GameException;
import com.bakholdin.siderealconfluence.exceptions.UserException;
import com.bakholdin.siderealconfluence.gameactions.GameAction;
import com.bakholdin.siderealconfluence.gameactions.GameActionDto;
import com.bakholdin.siderealconfluence.gameactions.GameActionOrder;
import com.bakholdin.siderealconfluence.gameactions.GameActionUpdateContainer;
import com.bakholdin.siderealconfluence.gameactions.dto.PlayerReadyDto;
import com.bakholdin.siderealconfluence.mapper.CardMapper;
import com.bakholdin.siderealconfluence.mapper.CloningMapper;
import com.bakholdin.siderealconfluence.mapper.ResourcesMapper;
import com.bakholdin.siderealconfluence.repository.CardRepository;
import com.bakholdin.siderealconfluence.repository.GameRepository;
import com.bakholdin.siderealconfluence.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(GameActionOrder.START_GAME)
@RequiredArgsConstructor
public class StartGameAction implements GameAction {
    private final GameRepository gameRepository;
    private final ResourcesMapper resourcesMapper;
    private final CloningMapper cloningMapper;
    private final PlayerRepository playerRepository;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public void validate(UserDto userDto, GameActionDto gameActionDto) {
        if (userDto.getGame() == null) {
            throw new UserException(String.format("%s is not in a game", userDto.getUsername()));
        }
        gameRepository.getById(userDto.getGame().getId());
    }

    @Override
    public void resolve(UserDto userDto, GameActionDto gameActionDto, GameActionUpdateContainer gameActionUpdateContainer) {
        Game game = gameRepository.getById(userDto.getGame().getId());
        // only start game if all players are ready and in lobby
        if (game.getState() != GameState.Lobby || game.getPlayers().stream().anyMatch(p -> !p.getReady())) {
            return;
        }
        if (game.getPlayers().stream().anyMatch(p -> p.getRace() == null)) {
            throw new GameException(game.getId(), "Cannot start game until players have selected a race");
        }

        initializeGame(gameActionUpdateContainer, game);
        game.getPlayers().forEach(player -> {
            initializePlayer(gameActionUpdateContainer, player);
        });

    }

    private void initializeGame(GameActionUpdateContainer gameActionUpdateContainer, Game game) {
        UpdateGameDto.Builder updateGameBuilder = gameActionUpdateContainer.getUpdateGameBuilder();
        game.setState(GameState.InProgress);
        updateGameBuilder.state(GameState.InProgress);
        game.setPhase(GamePhase.Trade);
        updateGameBuilder.phase(GamePhase.Trade);

        game.setColonyDeck(getAndShuffleColonies());
        game.setResearchTeamDeck(getAndShuffleResearchTeams());

        gameRepository.save(game);
    }

    private List<ActiveCard> getAndShuffleColonies() {
        CardType cardType = CardType.Colony;
        List<ActiveCard> colonies = getActiveCards(cardType);
        Collections.shuffle(colonies);
        return colonies;
    }

    private List<ActiveCard> getAndShuffleResearchTeams() {
        List<ActiveCard> researchTeams = getActiveCards(CardType.ResearchTeam);
        Collections.shuffle(researchTeams);
        // order by era, preserving relative order from the previous shuffle
        researchTeams.sort(Comparator.comparingInt(c -> c.getCard().getResearchTeam().getEra()));
        return researchTeams;
    }

    private List<ActiveCard> getActiveCards(CardType cardType) {
        return cardRepository.findByCardType(cardType).stream()
                .map(cardMapper::toActiveCard)
                .collect(Collectors.toList());
    }

    private void initializePlayer(GameActionUpdateContainer gameActionUpdateContainer, Player player) {
        UpdatePlayerDto.Builder updatePlayerBuilder = gameActionUpdateContainer.getUpdatePlayerBuilder(player.getUser());

        player.setResources(cloningMapper.clone(player.getRace().getStartingResources()));
        updatePlayerBuilder.resources(resourcesMapper.toDto(player.getResources()));
        player.setDonations(new Resources());
        updatePlayerBuilder.donations(resourcesMapper.toDto(player.getDonations()));
        player.setReady(false);
        updatePlayerBuilder.ready(false);

        Set<ActiveCard> startingCards = cardRepository.findStartingConverterCards(player.getRace().getName())
                .stream()
                .map(cardMapper::toActiveCard)
                .collect(Collectors.toSet());
        player.setActiveCards(startingCards);
        updatePlayerBuilder.activeCards(cardMapper.toActiveCardDto(startingCards));

        playerRepository.save(player);
    }

    @Override
    public boolean supports(GameActionDto gameActionDto) {
        return gameActionDto instanceof PlayerReadyDto;
    }
}
