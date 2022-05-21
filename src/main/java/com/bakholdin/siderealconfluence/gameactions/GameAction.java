package com.bakholdin.siderealconfluence.gameactions;

import com.bakholdin.siderealconfluence.dto.UserDto;

public interface GameAction {
    void validate(UserDto userDto, GameActionDto gameActionDto);

    void resolve(UserDto userDto, GameActionDto gameActionDto, GameActionUpdateContainer gameActionUpdateContainer);

    boolean supports(GameActionDto gameActionDto);
}
