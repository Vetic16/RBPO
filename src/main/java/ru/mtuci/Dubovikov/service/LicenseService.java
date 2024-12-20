package ru.mtuci.Dubovikov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.Dubovikov.model.*;
import ru.mtuci.Dubovikov.repository.DeviceLicenseRepository;
import ru.mtuci.Dubovikov.repository.LicenseRepository;
import ru.mtuci.Dubovikov.utils.ActivationCodeGenerator;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final ProductService productService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceLicenseRepository deviceLicenseRepository;
    private LicenseService licenseService;

    public void someMethod(Device device, ApplicationUser authenticatedUser) {
        List<License> activeLicenses = licenseService.getActiveLicensesForDevice(device, authenticatedUser);
        // Работайте с активными лицензиями
    }

    // Существующий метод активации лицензии
    public Ticket activateLicense(String activationCode, Device device, ApplicationUser user) {
        License license = licenseRepository.findByCode(activationCode)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия не найдена"));

        validateActivation(license, device, user);
        createDeviceLicense(license, device);
        updateLicense(license);

        licenseHistoryService.recordLicenseChange(license, user, "Activated", "Лицензия активирована");
        return generateTicket(license, device);
    }

    // Новый метод создания лицензии
    public License createLicense(Long productId, ApplicationUser owner, Long licenseTypeId, String description, Integer duration) {
        // Проверяем существование продукта
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Продукт не найден");
        }

        // Проверяем существование типа лицензии
        LicenseType licenseType = licenseTypeService.getLicenseTypeById(licenseTypeId);
        if (licenseType == null) {
            throw new IllegalArgumentException("Тип лицензии не найден");
        }

        // Создаем новую лицензию
        License license = new License();
        license.setCode(ActivationCodeGenerator.generateCode());
        license.setOwner(owner);
        license.setProduct(product);
        license.setType(licenseType);
        license.setDescription(description);
        license.setDuration(duration != null ? duration : licenseType.getDefaultDuration());
        license.setFirstActivationDate(LocalDate.now());
        license.setEndingDate(LocalDate.now().plusDays(duration));
        license.setBlocked(false);

        licenseRepository.save(license);

        licenseHistoryService.recordLicenseChange(license, owner, "Создана", "Лицензия успешно создана");

        return license;
    }

    private void validateActivation(License license, Device device, ApplicationUser user) {
        if (license.getBlocked()) {
            throw new IllegalArgumentException("Активация невозможна: лицензия заблокирована");
        }
        if (!license.getOwnerId().equals(user.getId())) {
            throw new IllegalArgumentException("Активация невозможна: пользователь не является владельцем лицензии");
        }
        // Дополнительные проверки...
    }
    private void createDeviceLicense(License license, Device device) {
        // Проверяем, не существует ли уже связь
        boolean exists = deviceLicenseRepository.findAll().stream()
                .anyMatch(dl -> dl.getLicense().equals(license) && dl.getDevice().equals(device));
        if (exists) {
            throw new IllegalArgumentException("Это устройство уже связано с указанной лицензией");
        }

        // Создаем новую связь
        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setLicense(license);
        deviceLicense.setDevice(device);
        deviceLicense.setActivationDate(LocalDate.now()); // Устанавливаем дату активации

        // Сохраняем в репозиторий
        deviceLicenseRepository.save(deviceLicense);
    }
    private void updateLicense(License license) {
        // Обновление лицензии (например, установка даты активации)
        license.setFirstActivationDate(LocalDate.now());
        licenseRepository.save(license);
    }
    public Ticket renewLicense(String licenseKey, ApplicationUser user) {
        // Проверка ключа лицензии
        License license = licenseRepository.findByCode(licenseKey)
                .orElseThrow(() -> new IllegalArgumentException("Недействительный ключ лицензии"));

        // Проверка владельца лицензии
        if (!license.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Вы не являетесь владельцем данной лицензии");
        }

        // Проверка возможности продления
        if (license.getBlocked() || license.getEndingDate().isBefore(LocalDate.now())) {
            return generateRejectionTicket(license, "Продление невозможно: лицензия заблокирована или истекла");
        }

        // Обновление даты окончания
        LocalDate newExpirationDate = license.getEndingDate().plusDays(30); // Продление на 30 дней
        license.setEndingDate(newExpirationDate);
        licenseRepository.save(license);

        // Генерация подтверждающего тикета
        return generateConfirmationTicket(license);
    }

    private Ticket generateConfirmationTicket(License license) {
        return new Ticket(
                30L, // Время жизни тикета
                license.getFirstActivationDate(),
                license.getEndingDate(),
                license.getOwnerId(),
                null, // Устройство не указано
                license.getBlocked(),
                "Подпись подтверждения" // Подпись
        );
    }

    private Ticket generateRejectionTicket(License license, String reason) {
        System.err.println("Причина отказа: " + reason);
        return new Ticket(
                30L,
                license.getFirstActivationDate(),
                license.getEndingDate(),
                license.getOwnerId(),
                null,
                license.getBlocked(),
                "Подпись отказа" // Подпись
        );
    }
    private Ticket generateTicket(License license, Device device) {
        // Получаем текущую дату
        LocalDate currentDate = LocalDate.now();

        // Устанавливаем firstActivationDate как текущую дату
        license.setFirstActivationDate(currentDate);

        // Получаем endingDate из License и оставляем его как LocalDate
        LocalDate endingDate = license.getEndingDate(); // Предполагается, что getEndingDate возвращает LocalDate

        // Создаем и возвращаем новый Ticket
        return new Ticket(
                30L, // Время жизни тикета в секундах
                currentDate, // Дата активации
                endingDate, // Дата окончания
                license.getOwnerId(), // Идентификатор владельца
                device.getId(), // Идентификатор устройства
                license.getBlocked(), // Состояние заблокированности
                "Подпись" // Подпись тикета
        );
    }
    public Optional<License> findLicenseByCode(String activationCode) {
        return licenseRepository.findByCode(activationCode);
    }

    public List<License> getLicensesByUser(ApplicationUser user) {
        return licenseRepository.findAllByOwnerId(user.getId());
    }

    public void blockLicense(Long licenseId, ApplicationUser admin) {
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия не найдена"));
        license.setBlocked(true);
        licenseRepository.save(license);

        licenseHistoryService.recordLicenseChange(license, admin, "Blocked", "Лицензия была заблокирована администратором");
    }

    public List<License> getActiveLicensesForDevice(Device device, ApplicationUser authenticatedUser) {
        return licenseRepository.findAllByDeviceAndOwnerAndBlockedFalse(device, authenticatedUser);
    }
}

