package com.bakholdin.siderealconfluence.dto;

import lombok.Data;

@Data
public class PlayerDto {
    private UserDto user;
    private ResourcesDto resources;
    private ResourcesDto donations;
}
