package za.co.auth.service;

import za.co.auth.dto.AppUserDTO;
import za.co.auth.dto.NewPasswordDTO;

public interface UserService {
    void registerUser(AppUserDTO dto);

    void resetPassword(NewPasswordDTO dto);

    String authenticate(AppUserDTO dto);
}
