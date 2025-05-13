package za.co.user.service.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.co.user.service.entity.AppUserEntity;
import za.co.user.service.entity.PasswordResetToken;
import za.co.user.service.records.AppUserRecord;
import za.co.user.service.records.NewPasswordRecord;
import za.co.user.service.repository.PasswordResetTokenRepository;
import za.co.user.service.repository.UserRepository;
import za.co.user.service.security.JwtProvider;
import za.co.user.service.service.EmailService;
import za.co.user.service.service.UserService;
import za.co.user.service.utilities.Converter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordResetTokenServiceImpl passwordResetTokenService;

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtProvider jwtProvider,
                           EmailService emailService, PasswordResetTokenRepository tokenRepository, PasswordResetTokenServiceImpl passwordResetTokenService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.passwordResetTokenService = passwordResetTokenService;
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
                emailService.sendAccountActivationEmail(appUserRecord.email(), authenticate(appUserRecord))
        );
    }

    @Override
    public void passwordResetRequest(NewPasswordRecord newPasswordRecord) {
        if (!Objects.equals(newPasswordRecord.newPassword(), newPasswordRecord.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        AppUserEntity userEntity = Converter.optionalToEntity(
                userRepository.findByUsernameAndEmail(newPasswordRecord.username(), newPasswordRecord.email())
        );

        if (userEntity == null) {
            throw new IllegalArgumentException("User not found");
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(userEntity);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        tokenRepository.save(resetToken);

        CompletableFuture.runAsync(() ->
                emailService.sendPasswordResetEmail(newPasswordRecord.email(), token)
        );
    }

    @Override
    public String authenticate(AppUserRecord dto) {
        // Generate JWT token after successful authentication
        String token = jwtProvider.generateToken(jwtSecret, dto.username());// Generate token using JWT utility
        System.out.println("Generated token: " + token);
        return token;
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

    @Override
    public Boolean confirmPasswordReset(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);
        if (passwordResetToken == null) {
            return false;
        }

        String email = passwordResetToken.getUser().getEmail();
        if (email == null) {
            return false;
        }

        AppUserEntity userEntity = passwordResetToken.getUser();
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
        tokenRepository.delete(passwordResetToken);
        return true;
    }

    @Override
    public AppUserEntity findByUsername(String name) {
        Optional<AppUserEntity> user = userRepository.findByUsername(name);
        return Converter.optionalToEntity(user);
    }


}
