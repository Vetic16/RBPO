package ru.mtuci.Dubovikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.configuration.JwtTokenProvider;
import ru.mtuci.Dubovikov.model.ApplicationUser;
import ru.mtuci.Dubovikov.model.AuthenticationRequest;
import ru.mtuci.Dubovikov.model.AuthenticationResponse;
import ru.mtuci.Dubovikov.model.RegistrationRequest;
import ru.mtuci.Dubovikov.repository.UserRepository;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            String email = request.getEmail();
            ApplicationUser user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
            String token = jwtTokenProvider.createToken(email, user.getRole().getGrantedAuthorities());
            return ResponseEntity.ok(new AuthenticationResponse(email, token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already taken");
        }

        ApplicationUser  newUser  = new ApplicationUser ();
        newUser .setLogin(registrationRequest.getLogin());
        newUser .setEmail(registrationRequest.getEmail());
        newUser .setPassword_hash(passwordEncoder.encode(registrationRequest.getPassword()));
        newUser .setRole(registrationRequest.getRole());
        userRepository.save(newUser );
        return ResponseEntity.status(HttpStatus.CREATED).body("User  registered successfully");
    }
}
