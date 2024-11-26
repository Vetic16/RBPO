package ru.mtuci.Dubovikov.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String email;
    private String token;
}