package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo_2024_praktika.model.Session;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    // Поиск сессии по refreshToken и deviceId
    Optional<Session> findByRefreshTokenAndDeviceId(String refreshToken, String deviceId);

    // Метод для блокировки всех активных сессий пользователя, кроме уже использованных
    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.sessionStatus = 'REVOKED' WHERE s.user.id = :userId AND s.sessionStatus = 'ACTIVE'")
    void blockUserSessions(Long userId);

    // Проверка существования сессии по accessToken и deviceId
    boolean existsByAccessTokenAndDeviceId(String accessToken, String deviceId);

    // Проверка существования refreshToken по deviceId
    boolean existsByRefreshTokenAndDeviceId(String refreshToken, String deviceId);

    // Метод для получения всех сессий пользователя
    Iterable<Session> findByUser(ApplicationUser user);

    // Метод для получения активных сессий пользователя
    @Query("SELECT s FROM Session s WHERE s.user = :user AND s.sessionStatus = 'ACTIVE'")
    Iterable<Session> findActiveSessionsByUser(ApplicationUser user);
}
