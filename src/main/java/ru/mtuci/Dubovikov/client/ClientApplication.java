package ru.mtuci.Dubovikov.client;

import ru.mtuci.Dubovikov.model.Ticket;
import ru.mtuci.Dubovikov.service.AuthenticationService;
import ru.mtuci.Dubovikov.service.LicenseService;

public class ClientApplication {

    private final AuthenticationService authenticationService;
    private final LicenseService licenseService;

    public ClientApplication(AuthenticationService authenticationService, LicenseService licenseService) {
        this.authenticationService = authenticationService;
        this.licenseService = licenseService;
    }

    public void requestLicenseRenewal(String username, String password, String licenseKey) {
        try {
            // Аутентификация пользователя
            var user = authenticationService.authenticate(username, password);

            // Запрос на продление лицензии
            Ticket ticket = licenseService.renewLicense(licenseKey, user);

            // Отображение результата
            System.out.println("Продление успешно! Новая дата окончания: " + ticket.getExpirationDate());
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
        }
    }
}
