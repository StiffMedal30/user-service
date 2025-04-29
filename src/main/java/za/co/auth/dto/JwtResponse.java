package za.co.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String type = "Bearer"; // optional: hardcoded type
    private String username;

    public JwtResponse(String token, AppUserDTO dto) {
        this.token = token;
        this.username = dto.getUsername();
    }
}
