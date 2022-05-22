package com.bakholdin.siderealconfluence.dto;

import com.bakholdin.siderealconfluence.enums.GamePhase;
import lombok.Data;

@Data
public class ConverterDto {
    private GamePhase phase;
    private ResourcesDto input;
    private ResourcesDto output;
    private ResourcesDto donations;
}
