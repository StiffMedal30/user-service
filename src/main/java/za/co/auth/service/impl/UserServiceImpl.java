package za.co.auth.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.co.auth.entity.AppUserEntity;
import za.co.auth.records.AppUserRecord;
import za.co.auth.records.NewPasswordRecord;
import za.co.auth.repository.UserRepository;
import za.co.auth.security.JwtProvider;
import za.co.auth.service.Mailer;
import za.co.auth.service.UserService;
import za.co.auth.utilities.Converter;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final Mailer mailer;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public UserServiceImpl(PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           AuthenticationManager authenticationManager,
                           JwtProvider jwtProvider,
                           Mailer mailer) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.mailer = mailer;
    }

    @Override
    public void registerUser(AppUserRecord appUserRecord) {
        userRepository.findByEmail(appUserRecord.email())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email already in use");
                });
        AppUserEntity user = new AppUserEntity();
        user.setPassword(passwordEncoder.encode(appUserRecord.password()));
        user.setRole(appUserRecord.role());
        user.setUsername(appUserRecord.username());
        user.setEmail(appUserRecord.email());
        user.setName(appUserRecord.name());
        user.setCredentialsNonExpired(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setEnabled(false);

        userRepository.save(user);

        CompletableFuture.runAsync(() ->
                mailer.sendVerificationEmail(appUserRecord.email(), authenticate(appUserRecord))
        );
    }

    @Override
    public void resetPassword(NewPasswordRecord newPasswordRecord) {
        if (!Objects.equals(newPasswordRecord.newPassword(), newPasswordRecord.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        AppUserEntity userEntity = Converter.optionalToEntity(userRepository.findByUsernameAndEmail(newPasswordRecord.username(), newPasswordRecord.email()));
        if (userEntity == null) {
            throw new IllegalArgumentException("User not found");
        }

        userEntity.setPassword(passwordEncoder.encode(newPasswordRecord.newPassword()));
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setEnabled(true);
        userRepository.save(userEntity);

    }

    @Override
    public String authenticate(AppUserRecord dto) {
        // Generate JWT token after successful authentication
        return jwtProvider.generateToken(jwtSecret, dto.email()); // Generate token using JWT utility
    }

    @Override
    public AppUserRecord findUserById(Long userId) {
        Optional<AppUserEntity> user = userRepository.findById(userId);
        AppUserEntity userEntity = Converter.optionalToEntity(user);

        return new AppUserRecord(userEntity.getId(), userEntity.getUsername(), null, userEntity.getEmail(),
                userEntity.getName(), userEntity.getRole(), null, null);
    }

    @Override
    public void activateUser(AppUserRecord user) {
        AppUserEntity userEntity = Converter.optionalToEntity(userRepository.findByEmail(user.email()));
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
    }

    @Override
    public AppUserRecord findUserByEmail(String email) {
        Optional<AppUserEntity> user = userRepository.findByEmail(email);
        AppUserEntity userEntity = Converter.optionalToEntity(user);
        return new AppUserRecord(userEntity.getId(), userEntity.getUsername(), null, userEntity.getEmail(),
                userEntity.getName(), userEntity.getRole(), null, null);
    }
}
