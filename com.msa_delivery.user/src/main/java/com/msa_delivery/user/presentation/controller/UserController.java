package com.msa_delivery.user.presentation.controller;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
public class UserController {

    @GetMapping("/test")
    public String test(@RequestHeader(value = "X-User_Id", required = true) String userId,
                       @RequestHeader(value = "X-Username", required = true) String username,
                       @RequestHeader(value = "X-Role", required = true) String role) {
        return userId + " // " + username + " // " + role;
    }
}
