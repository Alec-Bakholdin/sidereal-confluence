package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GameMapper.class})
public interface UserMapper {

    @Mapping(target = "game", qualifiedByName = "toGameDto")
    UserDto toUserDto(User user);
}
