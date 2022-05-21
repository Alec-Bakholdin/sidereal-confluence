package com.bakholdin.siderealconfluence.dto;

import lombok.Data;

@Data
public class PlayerDto {
    private UserDto user;
    private Boolean ready;
    private ResourcesDto resources;
    private ResourcesDto donations;
    private RaceDto race;
}
