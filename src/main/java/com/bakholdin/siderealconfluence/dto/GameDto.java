package com.bakholdin.siderealconfluence.dto;

import lombok.Data;

import java.util.Set;

@Data
public class GameDto {
    private Long id;
    private Set<UserDto> users;
}
