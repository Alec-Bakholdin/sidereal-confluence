package com.bakholdin.siderealconfluence.controllers.socket;

import com.bakholdin.siderealconfluence.dto.UserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

public class SocketUtil {

    public static UserDto getUserDto(Principal principal) {
        return (UserDto) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }
}
