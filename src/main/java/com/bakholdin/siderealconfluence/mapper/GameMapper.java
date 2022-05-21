package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.GameDto;
import com.bakholdin.siderealconfluence.entity.Game;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class GameMapper {
    @AfterMapping
    protected void ignoreUserGames(Game game, @MappingTarget GameDto gameDto) {
        gameDto.getUsers()
                .forEach(user -> user.setGame(null));
    }

    public abstract GameDto toGameDto(Game game);
}