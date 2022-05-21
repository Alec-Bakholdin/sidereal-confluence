package com.bakholdin.siderealconfluence.gameactions.dto;

import com.bakholdin.siderealconfluence.enums.RaceType;
import com.bakholdin.siderealconfluence.gameactions.GameActionDto;
import lombok.Data;

@Data
public class ChooseRaceDto implements GameActionDto {
    private RaceType raceType;
}
