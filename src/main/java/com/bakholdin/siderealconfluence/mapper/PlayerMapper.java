package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.PlayerDto;
import com.bakholdin.siderealconfluence.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GameMapper.class})
public interface PlayerMapper {

    @Mapping(target = "user", qualifiedByName = "toSafeUserDto")
    PlayerDto toDto(Player player);
}
