package ru.mtuci.Dubovikov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Ticket {

    // Текущая дата сервера
    private LocalDateTime serverDate;

    // Время жизни тикета в секундах
    private Long ticketLifetime;

    // Дата активации лицензии
    private LocalDate activationDate;

    // Дата истечения лицензии
    private LocalDate expirationDate;

    // Идентификатор пользователя
    private Long userId;

    // Идентификатор устройства
    private Long deviceId;

    // Флаг блокировки лицензии
    private boolean isLicenseBlocked;

    // Цифровая подпись
    private String digitalSignature;

    // Конструктор, который может быть полезен для генерации тикета с текущей датой сервера
    public Ticket(Long ticketLifetime, LocalDate activationDate, LocalDate expirationDate, Long userId, Long deviceId, boolean isLicenseBlocked, String digitalSignature) {
        this.serverDate = LocalDateTime.now(); // Текущая дата сервера
        this.ticketLifetime = ticketLifetime;
        this.activationDate = activationDate;
        this.expirationDate = expirationDate;
        this.userId = userId;
        this.deviceId = deviceId;
        this.isLicenseBlocked = isLicenseBlocked;
        this.digitalSignature = digitalSignature;
    }
}
