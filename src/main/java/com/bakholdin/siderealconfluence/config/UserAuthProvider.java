package com.bakholdin.siderealconfluence.config;

import com.bakholdin.siderealconfluence.dto.CredentialsDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class UserAuthProvider implements AuthenticationProvider {
    private final AuthenticationService authenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDto userDto = null;

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            userDto = authenticationService.authenticate(new CredentialsDto(
                    (String) authentication.getPrincipal(),
                    (String) authentication.getCredentials()
            ));
        } else if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            userDto = authenticationService.findByToken((String) authentication.getPrincipal());
        }

        if (userDto == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(userDto, null, Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
