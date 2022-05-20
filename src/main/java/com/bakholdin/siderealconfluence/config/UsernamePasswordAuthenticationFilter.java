package com.bakholdin.siderealconfluence.config;

import com.bakholdin.siderealconfluence.dto.CredentialsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsernamePasswordAuthenticationFilter extends OncePerRequestFilter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if ("/login".equals(request.getServletPath()) && HttpMethod.POST.name().equals(request.getMethod())) {
            CredentialsDto credentialsDto = MAPPER.readValue(request.getInputStream(), CredentialsDto.class);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            credentialsDto.getUsername(),
                            credentialsDto.getPassword()
                    )
            );
        }

        filterChain.doFilter(request, response);
    }
}
