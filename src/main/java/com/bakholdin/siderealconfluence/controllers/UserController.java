package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.dto.UpdatePasswordDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public UserDto fetchCurrentUser(@AuthenticationPrincipal UserDto userDto) {
        return userDto;
    }

    @PostMapping("/updatePassword")
    public void updatePassword(
            @AuthenticationPrincipal UserDto userDto,
            @RequestBody UpdatePasswordDto updatePasswordDto) {
        userService.updatePassword(userDto, updatePasswordDto);
    }
}
