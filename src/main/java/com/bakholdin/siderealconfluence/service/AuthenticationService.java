package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.dto.CredentialsDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;

    public UserDto authenticate(CredentialsDto credentialsDto) {
        String encodedPassword = passwordEncoder.encode(CharBuffer.wrap("password"));
        UserDto userDto = findByUsername(credentialsDto.getUsername());
        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), encodedPassword)) {
            return userDto;
        }
        throw new RuntimeException("Invalid password");
    }

    public UserDto findByUsername(String username) {
        if ("milkudders".equals(username)) {
            return UserDto.builder()
                    .username("milkudders")
                    .name("Alec")
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }

    public UserDto findByToken(String token) {
        String username = jwtTokenService.getUsernameFromToken(token);
        return findByUsername(username);
    }
}
