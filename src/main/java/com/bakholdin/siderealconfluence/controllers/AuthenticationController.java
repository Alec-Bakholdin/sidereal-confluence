package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.config.CookieAuthenticationFilter;
import com.bakholdin.siderealconfluence.dto.RegisterDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.service.JwtTokenService;
import com.bakholdin.siderealconfluence.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@AuthenticationPrincipal UserDto userDto, HttpServletResponse response) {
        log.info("Logged in {}", userDto.getName());

        Cookie authCookie = new Cookie(CookieAuthenticationFilter.AUTH_COOKIE_NAME, jwtTokenService.generateJwtToken(userDto));
        authCookie.setHttpOnly(true);
        authCookie.setMaxAge((int) Duration.of(30, ChronoUnit.DAYS).toSeconds());
        authCookie.setPath("/");

        response.addCookie(authCookie);

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();

        Optional<Cookie> authCookie = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> cookie.getName().equals(CookieAuthenticationFilter.AUTH_COOKIE_NAME))
                .findFirst();

        authCookie.ifPresent(cookie -> cookie.setMaxAge(0));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody RegisterDto registerDto) {
        return userService.registerUser(registerDto);
    }
}
