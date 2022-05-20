package com.bakholdin.siderealconfluence.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@RequiredArgsConstructor
public class ErrorDto implements Serializable {
    private final String message;
}
