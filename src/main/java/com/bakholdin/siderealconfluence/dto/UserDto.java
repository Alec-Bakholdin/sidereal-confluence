package com.bakholdin.siderealconfluence.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.security.Principal;

@Data
@Builder
@AllArgsConstructor
public class UserDto implements Principal {
    private String username;

    @Override
    @JsonIgnore
    public String getName() {
        return username;
    }
}
