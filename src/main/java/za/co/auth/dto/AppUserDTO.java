package za.co.auth.dto;

import lombok.Getter;
import lombok.Setter;
import za.co.auth.enums.Role;

@Setter
@Getter
public class AppUserDTO {
    private String username;
    private String password;
    private String email;
    private String name;
    private Role role;
    private String adminUsername;
    private String ideaName;

}
