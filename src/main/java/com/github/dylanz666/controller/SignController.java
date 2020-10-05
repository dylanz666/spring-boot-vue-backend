package com.github.dylanz666.controller;

import com.github.dylanz666.domain.SignInResponse;
import com.github.dylanz666.domain.User;
import org.springframework.web.bind.annotation.*;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@RestController
public class SignController {
    @GetMapping("/ping")
    public String ping() {
        return "Success!";
    }

    @PostMapping("/login")
    public SignInResponse login(@RequestBody User user) {
        SignInResponse signInResponse = new SignInResponse();
        signInResponse.setUsername(user.getUsername());
        signInResponse.setUserType(user.getUserType());
        signInResponse.setMessage("success");
        signInResponse.setStatus("success");
        signInResponse.setCode(200);
        return signInResponse;
    }
}
