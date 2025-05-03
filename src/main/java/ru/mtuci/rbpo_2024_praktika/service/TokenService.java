package ru.mtuci.rbpo_2024_praktika.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.configuration.JwtTokenProvider;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.Session;
import ru.mtuci.rbpo_2024_praktika.repository.SessionRepository;
import ru.mtuci.rbpo_2024_praktika.repository.UserRepository;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    // Создание Access Token
    public String createAccessToken(ApplicationUser user) {
        long accessTokenExpiration = 3600000;
        return jwtTokenProvider.createToken(
                user.getEmail(),
                user.getRole().getGrantedAuthorities(),
                "access",
                accessTokenExpiration,
                null // У Access-токена нет deviceId
        );
    }

    public String createRefreshToken(ApplicationUser user, String deviceId) {
        long refreshTokenExpiration = 604800000;
        return jwtTokenProvider.createToken(
                user.getEmail(),
                user.getRole().getGrantedAuthorities(),
                "refresh",
                refreshTokenExpiration,
                deviceId // Refresh-токен привязан к deviceId
        );
    }

    // Сохранение новой сессии
    public void saveSession(ApplicationUser user, String accessToken, String refreshToken, String deviceId) {
        // Создаем новую сессию и передаем необходимые данные в конструктор
        Session session = new Session(accessToken, refreshToken);

        // Устанавливаем пользователя
        session.setUser(user); // Устанавливаем пользователя через метод setUser()

        // Устанавливаем остальные поля
        session.setDeviceId(deviceId);
        session.setExpiresAt(new Date(System.currentTimeMillis() + 604800000)); // Устанавливаем срок истечения (например, 7 дней)
        session.setSessionStatus(Session.SessionStatus.ACTIVE); // Сессия активна при создании
        session.setActiveDate(new Date()); // Устанавливаем дату активации сессии
        session.setUsed(false); // Лицензия не используется при создании сессии

        // Сохраняем сессию в базе данных
        sessionRepository.save(session);
    }
    // Обновление сессии по refreshToken и deviceId
    public Session refreshSession(String refreshToken, String deviceId) {
        // Ищем сессию по refreshToken и deviceId
        Session session = sessionRepository.findByRefreshTokenAndDeviceId(refreshToken, deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found or invalid refresh token"));

        // Проверяем активность сессии
        if (!session.isActive() || session.getUser() == null) {
            throw new IllegalArgumentException("Session is blocked or user not found");
        }

        // Генерируем новые токены
        String newAccessToken = createAccessToken(session.getUser());
        String newRefreshToken = createRefreshToken(session.getUser(), deviceId);

        // Обновляем сессию
        session.setAccessToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setCreatedAt(new Date());
        session.setExpiresAt(new Date(System.currentTimeMillis() + 604800000)); // Обновляем срок истечения

        sessionRepository.save(session);
        return session;
    }

    // Метод для генерации новой пары токенов и сохранения сессии
    public void issueTokenPair(ApplicationUser user, String deviceId) {
        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user, deviceId);

        saveSession(user, accessToken, refreshToken, deviceId);
    }

    // Метод для обновления пары токенов с проверкой сессии и блокировкой всех сессий при нарушении
    public Session refreshTokenPair(String refreshToken, String deviceId) {
        // Ищем сессию по refreshToken и deviceId
        Session session = sessionRepository.findByRefreshTokenAndDeviceId(refreshToken, deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found or invalid refresh token"));

        // Проверяем активность сессии
        if (!session.isActive() || session.getUser() == null) {
            blockAllSessionsForUser(session.getUser());
            throw new IllegalArgumentException("Session is blocked or user not found. All sessions for the user are now blocked.");
        }

        // Генерируем новые токены
        String newAccessToken = createAccessToken(session.getUser());
        String newRefreshToken = createRefreshToken(session.getUser(), deviceId);

        // Обновляем сессию
        session.setAccessToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setCreatedAt(new Date());
        session.setExpiresAt(new Date(System.currentTimeMillis() + 604800000)); // Обновляем срок истечения

        sessionRepository.save(session);
        return session;
    }

    // Метод для блокировки всех сессий пользователя
    public void blockAllSessionsForUser(ApplicationUser user) {
        // Получаем все сессии пользователя
        Iterable<Session> sessions = sessionRepository.findByUser(user);
        for (Session session : sessions) {
            session.setSessionStatus(Session.SessionStatus.REVOKED); // Устанавливаем статус "отозвана"
            sessionRepository.save(session);
        }
    }
}
