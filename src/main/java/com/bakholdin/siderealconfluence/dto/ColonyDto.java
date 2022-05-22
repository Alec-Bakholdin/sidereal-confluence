package com.bakholdin.siderealconfluence.dto;

import com.bakholdin.siderealconfluence.enums.ColonyType;
import lombok.Data;

@Data
public class ColonyDto {
    private ColonyType colonyType;
    private String name;

    private ColonyType frontType;
    private ConverterDto frontConverter;

    private ConverterDto upgradeConverter;

    private ColonyType backType;
    private ConverterDto backConverter;
}
