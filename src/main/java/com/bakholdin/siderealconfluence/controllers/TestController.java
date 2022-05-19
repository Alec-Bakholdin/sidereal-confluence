package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Log4j2
@Controller
@RequiredArgsConstructor
public class TestController {
    private final CardRepository cardRepository;

    @GetMapping("/test")
    private String getCardEntities(Principal principal) {
        log.info(principal.toString());
        return principal.toString();
    }

    @MessageMapping("/ws")
    @SendTo("/topic/greetings")
    public String greeting(String message) {
        return "Hello " + message;
    }

    @GetMapping("/csrf")
    public @ResponseBody String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrf.getToken();
    }
}
