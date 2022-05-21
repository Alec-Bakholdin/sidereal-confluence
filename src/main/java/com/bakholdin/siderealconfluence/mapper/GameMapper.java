package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.GameDto;
import com.bakholdin.siderealconfluence.dto.PlayerDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Game;
import com.bakholdin.siderealconfluence.entity.Player;
import com.bakholdin.siderealconfluence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GameMapper {
    @Named("toGameDto")
    @Mapping(target = "players", qualifiedByName = "toPlayerDtoMapWithoutGame")
    GameDto toGameDto(Game game);

    @Named("toPlayerDtoMapWithoutGame")
    default Map<String, PlayerDto> toPlayerDtoMapWithoutGame(Set<Player> players) {
        return players.stream()
                .collect(
                        Collectors.toMap(
                                player -> player.getUser().getUsername(),
                                this::toSafePlayerDto
                        )
                );
    }

    @Named("toSafePlayerDto")
    @Mapping(target = "user", qualifiedByName = "toSafeUserDto")
    PlayerDto toSafePlayerDto(Player player);

    @Named("toSafeUserDto")
    @Mapping(target = "game", ignore = true)
    UserDto toSafeUserDto(User user);
}