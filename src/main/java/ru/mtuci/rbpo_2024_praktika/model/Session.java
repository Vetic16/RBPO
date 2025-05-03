package ru.mtuci.rbpo_2024_praktika.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessToken;
    private String refreshToken;
    private String deviceId;
    private Date createdAt;

    @Enumerated(EnumType.STRING) // Используем Enum для статуса сессии
    @Column(nullable = false)
    private SessionStatus sessionStatus = SessionStatus.ACTIVE; // Статус сессии, по умолчанию ACTIVE

    @Column(nullable = false)
    private Date expiresAt; // Поле для срока истечения сессии

    @Column(nullable = false)
    private Date activeDate; // Поле для даты активации сессии (новое поле)

    @Column(nullable = false)
    private boolean used; // Поле для пометки сессии как использованной (булевое значение)

    @Version
    private Long version; // Версия для поддержки версионирования

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // Убираем insertable = false, updatable = false
    private ApplicationUser user; // Связь с пользователем

    // Конструкторы
    public Session() {
        this.createdAt = new Date();
    }

    public Session(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.createdAt = new Date();
    }

    // Метод для проверки активности сессии
    public boolean isActive() {
        return this.sessionStatus == SessionStatus.ACTIVE && (this.expiresAt == null || this.expiresAt.after(new Date()));
    }

    // Метод для пометки сессии как использованной
    public void setUsed(boolean used) {
        this.used = used;
        if (used) {
            this.sessionStatus = SessionStatus.USED;
        }
    }

    // Enum для статусов сессии
    public enum SessionStatus {
        ACTIVE,  // Сессия активна
        USED,    // Сессия использована
        REVOKED  // Сессия отозвана
    }
}