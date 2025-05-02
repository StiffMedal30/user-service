package za.co.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.auth.records.AppUserRecord;
import za.co.auth.records.JwtResponseRecord;
import za.co.auth.records.NewPasswordRecord;
import za.co.auth.service.UserService;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody AppUserRecord registerRequest) {
        try {
            userService.registerUser(registerRequest);
            return ResponseEntity.ok(Map.of("message",
                    "Thank you for registering at NovaFlow. Please check your email to activate your account."));
        } catch (RuntimeException e) {
            if (Objects.equals(e.getMessage(), "User is disabled")) {
                return ResponseEntity.ok().body(Map.of("message",
                        "Thank you for registering at NovaFlow. Please check your email to activate your account."));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody NewPasswordRecord registerRequest) {
        try {
            userService.resetPassword(registerRequest);
            return ResponseEntity.ok(Map.of("message", "User password reset was done successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AppUserRecord dto) {
        try {
            String jwt = userService.authenticate(dto);
            return ResponseEntity.ok(new JwtResponseRecord(jwt, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/account/activate")
    public ResponseEntity<Map<String, String>> activateAccount(@RequestBody Map<String, String> token) {
        try {
//            userService.activateAccount(token.get("token"));
            return ResponseEntity.ok(Map.of("message", "User account was activated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
