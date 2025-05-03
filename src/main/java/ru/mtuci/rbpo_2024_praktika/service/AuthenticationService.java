package ru.mtuci.rbpo_2024_praktika.service;

import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;

@Service
public class AuthenticationService {

    public ApplicationUser authenticate(String username, String password) {
        if ("user".equals(username) && "password".equals(password)) {
            return new ApplicationUser(1L, "user", "User Name");
        }
        throw new IllegalArgumentException("Ошибка аутентификации: неверные учётные данные");
    }
}
