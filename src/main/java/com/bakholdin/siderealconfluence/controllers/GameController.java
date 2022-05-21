package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.dto.DestroyGameDto;
import com.bakholdin.siderealconfluence.dto.GameDto;
import com.bakholdin.siderealconfluence.dto.JoinGameDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Game;
import com.bakholdin.siderealconfluence.mapper.GameMapper;
import com.bakholdin.siderealconfluence.repository.GameRepository;
import com.bakholdin.siderealconfluence.service.GameService;
import com.bakholdin.siderealconfluence.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameRepository gameRepository;
    private final GameService gameService;
    private final GameMapper gameMapper;
    private final UserService userService;

    @GetMapping("/{id}")
    public GameDto gameById(@PathVariable Long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new RuntimeException("Game not found"));
        return gameMapper.toGameDto(game);
    }

    @PostMapping("/create")
    public GameDto create(@AuthenticationPrincipal UserDto userDto) {
        Game game = gameService.createGame();
        JoinGameDto joinGameDto = JoinGameDto.builder().id(game.getId()).build();
        Game gameWithUser = gameService.addUserToGame(joinGameDto, userDto);
        return gameMapper.toGameDto(gameWithUser);
    }

    @PostMapping("/join")
    public GameDto join(@AuthenticationPrincipal UserDto userDto,
                        @RequestBody JoinGameDto joinGameDto) {
        Game game = gameService.addUserToGame(joinGameDto, userDto);
        return gameMapper.toGameDto(game);
    }

    @PostMapping("/destroy")
    public void destroy(@RequestBody DestroyGameDto destroyGameDto) {
        gameService.destroyGame(destroyGameDto);
    }

}
