package com.bakholdin.siderealconfluence.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ActiveCardDto {
    private UUID id;
    private Boolean upgraded;
    private CardDto card;
}
