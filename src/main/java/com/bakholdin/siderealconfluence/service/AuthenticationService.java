package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.dto.CredentialsDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.User;
import com.bakholdin.siderealconfluence.mapper.UserMapper;
import com.bakholdin.siderealconfluence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto authenticateUserPassword(CredentialsDto credentialsDto) {
        User user = userRepository.findByUsername(credentialsDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(credentialsDto.getPassword(), user.getPassword())) {
            return userMapper.toUserDto(user);
        }

        throw new RuntimeException("Invalid password");
    }

    @Transactional
    public UserDto authenticateJwtToken(String token) {
        String username = jwtTokenService.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserDto(user);
    }
}
