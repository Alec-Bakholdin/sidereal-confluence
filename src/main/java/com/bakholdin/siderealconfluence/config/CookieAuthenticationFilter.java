package com.bakholdin.siderealconfluence.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

public class CookieAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_COOKIE_NAME = "auth";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!"/register".equals(request.getServletPath())) {
            Optional<Cookie> cookieAuth = Stream.of(Optional.ofNullable(request.getCookies())
                            .orElse(new Cookie[0]))
                    .filter(cookie -> cookie.getName().equals(AUTH_COOKIE_NAME))
                    .findFirst();

            cookieAuth.ifPresent(cookie ->
                    SecurityContextHolder.getContext().setAuthentication(
                            new PreAuthenticatedAuthenticationToken(cookie.getValue(), null)
                    ));
        }

        filterChain.doFilter(request, response);
    }
}
