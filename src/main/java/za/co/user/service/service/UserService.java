package za.co.user.service.service;

import za.co.user.service.records.AppUserRecord;
import za.co.user.service.records.NewPasswordRecord;

public interface UserService {
    void registerUser(AppUserRecord dto);

    void resetPassword(NewPasswordRecord dto);

    String authenticate(AppUserRecord dto);

    AppUserRecord findUserById(Long userId);

    void activateUser(AppUserRecord user);

    AppUserRecord findUserByEmail(String email);
}
