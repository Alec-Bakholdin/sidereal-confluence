package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);
}
