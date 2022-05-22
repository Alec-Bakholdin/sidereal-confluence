package com.bakholdin.siderealconfluence.dto;

import com.bakholdin.siderealconfluence.enums.RaceType;
import lombok.Data;

import java.util.Set;

@Data
public class ConverterCardDto {
    private Integer era;
    private Boolean starting;
    private String upgradeTech1;
    private String upgradeTech2;
    private RaceType race;

    private String frontName;
    private Set<ConverterDto> frontConverters;
    private String backName;
    private Set<ConverterDto> backConverters;
}

