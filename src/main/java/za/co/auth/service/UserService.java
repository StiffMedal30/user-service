package za.co.auth.service;

import za.co.auth.records.AppUserRecord;
import za.co.auth.records.NewPasswordRecord;

public interface UserService {
    void registerUser(AppUserRecord dto);

    void resetPassword(NewPasswordRecord dto);

    String authenticate(AppUserRecord dto);

    AppUserRecord findUserById(Long userId);

    void activateUser(AppUserRecord user);

    AppUserRecord findUserByEmail(String email);
}
