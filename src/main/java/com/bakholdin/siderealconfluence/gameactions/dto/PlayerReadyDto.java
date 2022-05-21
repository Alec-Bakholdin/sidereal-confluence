package com.bakholdin.siderealconfluence.gameactions.dto;

import com.bakholdin.siderealconfluence.gameactions.GameActionDto;
import lombok.Data;

@Data
public class PlayerReadyDto implements GameActionDto {
    private boolean ready;
}
