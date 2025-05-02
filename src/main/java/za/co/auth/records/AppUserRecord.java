package za.co.auth.records;

import za.co.auth.enums.Role;


public record AppUserRecord(
        Long id,
        String username,
        String password,
        String email,
        String name,
        Role role,
        String adminUsername,
        String ideaName
) {
}
