package com.bakholdin.siderealconfluence.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder(builderClassName = "Builder")
public class UpdatePlayerDto {
    private UserDto user;
    private Boolean ready;
    private ResourcesDto resources;
    private ResourcesDto donations;
    private RaceDto race;
    private Set<ActiveCardDto> activeCards;
}
