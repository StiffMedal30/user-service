package za.co.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    public String getJWT_SECRET() {
        return JWT_SECRET;
    }

    public void setJWT_SECRET(String JWT_SECRET) {
        this.JWT_SECRET = JWT_SECRET;
    }

    // Method to generate JWT token
    public String generateToken(String securityKey, String email) {
        return Jwts.builder()
                .setSubject(email) // Set the username as subject
                .setIssuedAt(new Date()) // Set the issued timestamp
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expiry time (10 hours)
                .signWith(SignatureAlgorithm.HS512, securityKey) // Signing the JWT with the secret key
                .compact();
    }

    public String getEmailFromToken(String token) {
        try {
            // Remove Bearer prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
