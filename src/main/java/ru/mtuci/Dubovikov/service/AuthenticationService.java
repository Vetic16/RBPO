package ru.mtuci.Dubovikov.service;

import org.springframework.stereotype.Service;
import ru.mtuci.Dubovikov.model.ApplicationUser;

@Service
public class AuthenticationService {

    public ApplicationUser authenticate(String username, String password) {
        // Пример проверки учётных данных (здесь может быть доступ к базе данных)
        if ("user".equals(username) && "password".equals(password)) {
            return new ApplicationUser(1L, "user", "User Name");
        }
        throw new IllegalArgumentException("Ошибка аутентификации: неверные учётные данные");
    }
}
