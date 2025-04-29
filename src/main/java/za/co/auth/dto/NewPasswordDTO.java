package za.co.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewPasswordDTO {
    private String username;
    private String email;
    private String newPassword;
    private String confirmPassword;
}
