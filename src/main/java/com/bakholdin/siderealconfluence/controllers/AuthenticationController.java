package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.config.CookieAuthenticationFilter;
import com.bakholdin.siderealconfluence.dto.SignUpDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.service.JwtTokenService;
import com.bakholdin.siderealconfluence.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Stream;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    @PostMapping("/signIn")
    public ResponseEntity<UserDto> signIn(@AuthenticationPrincipal UserDto userDto, HttpServletResponse response) {
        response.addCookie(jwtTokenService.generateJwtCookie(userDto));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/signUp")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpDto signUpDto, HttpServletResponse response) {
        UserDto userDto = userService.registerUser(signUpDto);
        response.addCookie(jwtTokenService.generateJwtCookie(userDto));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/signOut")
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        //SecurityContextHolder.clearContext();

        Optional<Cookie> authCookie = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> cookie.getName().equals(CookieAuthenticationFilter.AUTH_COOKIE_NAME))
                .findFirst();
        authCookie.ifPresent(cookie -> cookie.setMaxAge(0));

        return ResponseEntity.noContent().build();
    }

}
