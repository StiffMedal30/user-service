package za.co.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    // Method to generate JWT token
    public String generateToken(Authentication authentication, String securityKey) {
        return Jwts.builder()
                .setSubject(authentication.getName()) // Set the username as subject
                .setIssuedAt(new Date()) // Set the issued timestamp
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expiry time (10 hours)
                .signWith(SignatureAlgorithm.HS512, securityKey) // Signing the JWT with the secret key
                .compact();
    }
}
