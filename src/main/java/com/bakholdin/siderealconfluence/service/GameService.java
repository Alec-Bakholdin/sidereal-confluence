package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.dto.DestroyGameDto;
import com.bakholdin.siderealconfluence.dto.JoinGameDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Game;
import com.bakholdin.siderealconfluence.entity.Player;
import com.bakholdin.siderealconfluence.entity.User;
import com.bakholdin.siderealconfluence.repository.GameRepository;
import com.bakholdin.siderealconfluence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public Game createGame() {
        Game game = new Game();
        gameRepository.save(game);
        gameRepository.flush();
        return game;
    }

    public Game addUserToGame(JoinGameDto joinGameDto, UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getGame() != null) {
            throw new RuntimeException("User already in game");
        }
        Game game = gameRepository.findById(joinGameDto.getId())
                .orElseThrow(() -> new RuntimeException("Game not found"));
        Player player = new Player();
        player.setUser(user);
        game.getPlayers().add(player);
        user.setGame(game);
        gameRepository.save(game);
        userRepository.save(user);
        return game;
    }

    public void destroyGame(DestroyGameDto destroyGameDto) {
        Game game = gameRepository.findById(destroyGameDto.getId())
                .orElseThrow(() -> new RuntimeException("Game not found"));
        game.getPlayers().forEach(player -> {
            player.getUser().setGame(null);
            userRepository.save(player.getUser());
        });
        gameRepository.delete(game);
    }
}
