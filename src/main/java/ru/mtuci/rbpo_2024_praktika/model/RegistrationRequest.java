package ru.mtuci.rbpo_2024_praktika.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private String login;
    private String email;
    private String password;
    private ApplicationRole role;
}