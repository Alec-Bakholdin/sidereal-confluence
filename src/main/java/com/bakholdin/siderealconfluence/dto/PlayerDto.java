package com.bakholdin.siderealconfluence.dto;

import lombok.Data;

@Data
public class PlayerDto {
    private Long id;
    private ResourcesDto resources;
    private ResourcesDto donations;
    private UserDto user;
}
