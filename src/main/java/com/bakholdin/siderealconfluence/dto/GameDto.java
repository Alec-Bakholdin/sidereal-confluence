package com.bakholdin.siderealconfluence.dto;

import com.bakholdin.siderealconfluence.enums.GamePhase;
import com.bakholdin.siderealconfluence.enums.GameState;
import lombok.Data;

import java.util.Set;

@Data
public class GameDto {
    private Long id;
    private GameState state;
    private GamePhase phase;

    private Set<UserDto> users;
}
