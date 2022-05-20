package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.config.CookieAuthenticationFilter;
import com.bakholdin.siderealconfluence.data.DataUtils;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

import static com.bakholdin.siderealconfluence.controllers.model.SessionHeaderKeys.SESSION_ID;

@Log4j2
@RestController
@RequiredArgsConstructor
public class TestController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtTokenService jwtTokenService;

    @GetMapping("/test")
    public UserDto getUser(@AuthenticationPrincipal UserDto userDto) {
        log.info("Received request");
        return userDto;
    }

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

    @MessageMapping("/test2")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(@Payload String message, SimpMessageHeaderAccessor headers) {
        String sessionId = DataUtils.getSessionHeader(headers, SESSION_ID);
        log.info("Received message from {}: {}", sessionId, message);
        return "Hello, " + message + "!";
    }

    @MessageMapping("/test3")
    public void testSessionIdWithoutSendToUser(@Payload String message, Principal principal) {
        log.info("Received message from {}: {}", principal.getName(), message);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", "Hello, " + message + "!");
    }
}
