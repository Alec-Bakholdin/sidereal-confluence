package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.GameDto;
import com.bakholdin.siderealconfluence.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class})
public interface GameMapper {
    @Named("toGameDto")
    @Mapping(target = "players", qualifiedByName = "toPlayerDtoWithoutGame")
    GameDto toGameDto(Game game);
}