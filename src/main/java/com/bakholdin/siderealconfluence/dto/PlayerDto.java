package com.bakholdin.siderealconfluence.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PlayerDto {
    private UserDto user;
    private Boolean ready;
    private ResourcesDto resources;
    private ResourcesDto donations;
    private RaceDto race;
    private Set<ActiveCardDto> activeCards;
}
