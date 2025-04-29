package za.co.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.auth.dto.AppUserDTO;
import za.co.auth.dto.JwtResponse;
import za.co.auth.dto.NewPasswordDTO;
import za.co.auth.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody AppUserDTO registerRequest) {
        try{
            userService.registerUser(registerRequest);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody NewPasswordDTO registerRequest) {
        try {
            userService.resetPassword(registerRequest);
            return ResponseEntity.ok(Map.of("message", "User password reset was done successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AppUserDTO dto) {
        try {
            String jwt = userService.authenticate(dto);
            return ResponseEntity.ok(new JwtResponse(jwt, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
