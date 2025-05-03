package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.AuthenticationRequest;
import ru.mtuci.rbpo_2024_praktika.model.AuthenticationResponse;
import ru.mtuci.rbpo_2024_praktika.model.RegistrationRequest;
import ru.mtuci.rbpo_2024_praktika.repository.UserRepository;
import ru.mtuci.rbpo_2024_praktika.service.TokenService;
import ru.mtuci.rbpo_2024_praktika.model.Session;
import ru.mtuci.rbpo_2024_praktika.model.RefreshRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request,
                                   @RequestHeader("Device-Id") String deviceId) {
        try {
            String email = request.getEmail();
            ApplicationUser user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));

            // Генерация Access и Refresh токенов
            String accessToken = tokenService.createAccessToken(user);
            String refreshToken = tokenService.createRefreshToken(user, deviceId);

            // Сохранение сессии в БД
            tokenService.saveSession(user, accessToken, refreshToken, deviceId);

            // Возвращаем пару токенов
            return ResponseEntity.ok(new AuthenticationResponse(user.getEmail(), accessToken, refreshToken));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already taken");
        }

        ApplicationUser newUser = new ApplicationUser();
        newUser.setUsername(registrationRequest.getLogin());
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setPassword_hash(passwordEncoder.encode(registrationRequest.getPassword()));
        newUser.setRole(registrationRequest.getRole());

        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshTokens(@RequestBody RefreshRequest refreshRequest) {
        try {
            // Получаем refreshToken и deviceId из запроса
            String refreshToken = refreshRequest.getRefreshToken();
            String deviceId = refreshRequest.getDeviceId();

            // Ищем сессию по refreshToken и deviceId
            Session newSession = tokenService.refreshSession(refreshToken, deviceId);

            // Получаем пользователя из сессии
            ApplicationUser user = userRepository.findById(newSession.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Возвращаем новые токены
            return ResponseEntity.ok(new AuthenticationResponse(user.getEmail(), newSession.getAccessToken(), newSession.getRefreshToken()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or reused refresh token");
        }
    }
}
