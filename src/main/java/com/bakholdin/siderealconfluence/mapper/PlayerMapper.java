package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.PlayerDto;
import com.bakholdin.siderealconfluence.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PlayerMapper {
    PlayerDto toPlayerDto(Player player);

    @Named("toPlayerDtoWithoutGame")
    @Mapping(target = "user", qualifiedByName = "toUserDtoWithoutGame")
    PlayerDto toPlayerDtoWithoutGame(Player player);
}
