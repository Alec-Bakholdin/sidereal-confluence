package com.bakholdin.siderealconfluence.controllers.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionPayload {
    private int httpStatusCode;
    private String error;
}
