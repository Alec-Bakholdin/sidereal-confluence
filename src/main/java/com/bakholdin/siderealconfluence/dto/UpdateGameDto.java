package com.bakholdin.siderealconfluence.dto;

import com.bakholdin.siderealconfluence.enums.GamePhase;
import com.bakholdin.siderealconfluence.enums.GameState;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateGameDto {
    private GameState state;
    private GamePhase phase;
}
