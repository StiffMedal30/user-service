package za.co.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.co.auth.dto.AppUserDTO;
import za.co.auth.dto.NewPasswordDTO;
import za.co.auth.entity.AppUserEntity;
import za.co.auth.repository.UserRepository;
import za.co.auth.security.JwtProvider;
import za.co.auth.service.UserService;
import za.co.auth.utilities.Converter;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
                           AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void registerUser(AppUserDTO registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email already in use");
                });
        AppUserEntity user = new AppUserEntity();
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void resetPassword(NewPasswordDTO dto) {
        if (!Objects.equals(dto.getNewPassword(), dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        AppUserEntity userEntity = Converter.optionalToEntity(userRepository.findByUsernameAndEmail(dto.getUsername(), dto.getEmail()));
        if (userEntity == null) {
            throw new IllegalArgumentException("User not found");
        }

        userEntity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setEnabled(true);
        userRepository.save(userEntity);

    }

    @Override
    public String authenticate(AppUserDTO dto) {
        // Creating authentication token from the request
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

        // Authenticating the user
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // Storing authentication details in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token after successful authentication
        return jwtProvider.generateToken(authentication, jwtSecret); // Generate token using JWT utility
    }
}
