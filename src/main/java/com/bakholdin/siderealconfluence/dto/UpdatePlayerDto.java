package com.bakholdin.siderealconfluence.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePlayerDto {
    private UserDto user;
    private ResourcesDto resources;
    private ResourcesDto donations;
    private RaceDto race;
}
