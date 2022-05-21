package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.GameDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Game;
import com.bakholdin.siderealconfluence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "game", qualifiedByName = "toSafeGameDto")
    public abstract UserDto toUserDto(User user);

    @Named("toUserDtoWithoutGame")
    @Mapping(target = "game", expression = "java(null)")
    protected abstract UserDto toUserDtoWithoutGame(User user);

    @Named("toSafeGameDto")
    @Mapping(target = "users", qualifiedByName = "toUserDtoWithoutGame")
    protected abstract GameDto toSafeGameDto(Game game);
}
