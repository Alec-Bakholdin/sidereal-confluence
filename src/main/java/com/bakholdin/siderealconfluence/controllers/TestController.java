package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Log4j2
@Controller
@RequiredArgsConstructor
public class TestController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/test2")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(@Payload String message, @AuthenticationPrincipal Principal principal) {
        UserDto userDto = (UserDto) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        log.info("Received message from {}: {}", userDto.getUsername(), message);
        return "Hello, " + userDto.getName() + "!";
    }

    @MessageMapping("/test3")
    public void testSessionIdWithoutSendToUser(@Payload String message, @AuthenticationPrincipal Principal principal) {
        log.info("Received message from {}: {}", principal.getName(), message);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", "Hello, " + principal.getName() + "!");
    }
}
