package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.Game;
import com.bakholdin.siderealconfluence.entity.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public abstract class UserMapper {

    @BeforeMapping
    protected void ignoreGameUserRecursion(Game game) {
        if (game != null && game.getUsers() != null) {
            game.getUsers().forEach(user -> user.setGame(null));
        }
    }

    public abstract UserDto toUserDto(User user);
}
