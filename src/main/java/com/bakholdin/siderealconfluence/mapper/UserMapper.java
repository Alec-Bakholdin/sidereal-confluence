package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.PlayerDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Player;
import com.bakholdin.siderealconfluence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {GameMapper.class})
public interface UserMapper {

    @Mapping(target = "game", qualifiedByName = "toGameDto")
    UserDto toUserDto(User user);

    @Mapping(target = "game", ignore = true)
    UserDto toUsernameOnly(UserDto user);

    @Named("toPlayerNoUserDto")
    @Mapping(target = "user", ignore = true)
    PlayerDto toPlayerDto(Player player);
}
