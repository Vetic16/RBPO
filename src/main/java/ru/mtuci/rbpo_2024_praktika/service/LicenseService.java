package ru.mtuci.rbpo_2024_praktika.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.*;
import ru.mtuci.rbpo_2024_praktika.repository.DeviceLicenseRepository;
import ru.mtuci.rbpo_2024_praktika.repository.LicenseRepository;
import ru.mtuci.rbpo_2024_praktika.utils.ActivationCodeGenerator;

import java.security.PrivateKey;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final ProductService productService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceLicenseRepository deviceLicenseRepository;

    public Ticket activateLicense(String activationCode, Device device, ApplicationUser user) {
        License license = licenseRepository.findByCode(activationCode)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия не найдена"));

        validateActivation(license, device, user);
        createDeviceLicense(license, device);

        if (license.getFirstActivationDate() == null) {
            license.setFirstActivationDate(LocalDate.now());
        }

        int deviceCount = deviceLicenseRepository.countByLicenseId(license.getId());
        int maxDeviceCount = license.getMaxDeviceCount();

        int remainingDevices = maxDeviceCount - 1;

        if (remainingDevices <= 0) {
            throw new IllegalArgumentException("Превышено максимальное количество устройств для данной лицензии");
        }

        updateLicense(license, remainingDevices);

        license.setDeviceCount(deviceCount);
        license.setEndingDate(license.getFirstActivationDate().plusDays(license.getDuration()));
        license.setUser(user);

        licenseRepository.save(license);
        licenseHistoryService.recordLicenseChange(license, user, "Activated", "Лицензия активирована");

        System.out.println("Оставшееся количество устройств для активации: " + remainingDevices);

        String deviceIds = getDeviceIdsForLicense(license);
        return generateTicket(license, deviceIds, null);

    }

    private String getDeviceIdsForLicense(License license) {
        List<DeviceLicense> deviceLicenses = deviceLicenseRepository.findAllByLicenseId(license.getId());

        return deviceLicenses.stream()
                .map(deviceLicense -> deviceLicense.getDevice().getId().toString())
                .collect(Collectors.joining(","));
    }

    public License createLicense(Long productId, ApplicationUser owner, Long licenseTypeId, String description, Integer duration) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Продукт не найден");
        }
        LicenseType licenseType = licenseTypeService.getLicenseTypeById(licenseTypeId);
        if (licenseType == null) {
            throw new IllegalArgumentException("Тип лицензии не найден");
        }

        License license = new License();
        license.setCode(ActivationCodeGenerator.generateCode());
        license.setOwner(owner);
        license.setDeviceCount(0);
        license.setMaxDeviceCount(10);
        license.setProduct(product);
        license.setType(licenseType);
        license.setDescription(description);
        license.setDuration(duration != null ? duration : licenseType.getDefaultDuration());
        license.setBlocked(false);

        licenseRepository.save(license);
        licenseHistoryService.recordLicenseChange(license, owner, "Создана", "Лицензия успешно создана");

        return license;
    }

    public void updateLicense(License license, int remainingDevices) {
        license.setMaxDeviceCount(remainingDevices);
        licenseRepository.save(license);
    }

    private void validateActivation(License license, Device device, ApplicationUser user) {
        if (license.getBlocked()) {
            throw new IllegalArgumentException("Активация невозможна: лицензия заблокирована");
        }
        if (!license.getOwnerId().equals(user.getId())) {
            throw new IllegalArgumentException("Активация невозможна: пользователь не является владельцем лицензии");
        }
    }

    private void createDeviceLicense(License license, Device device) {
        boolean exists = deviceLicenseRepository.findAll().stream()
                .anyMatch(dl -> dl.getLicense().equals(license) && dl.getDevice().equals(device));
        if (exists) {
            throw new IllegalArgumentException("Это устройство уже связано с указанной лицензией");
        }

        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setLicense(license);
        deviceLicense.setDevice(device);
        deviceLicense.setActivationDate(LocalDate.now());
        deviceLicenseRepository.save(deviceLicense);
    }

    public Ticket updateOrRenewLicense(String licenseKey, ApplicationUser user) {
        License license = licenseRepository.findByCode(licenseKey)
                .orElseThrow(() -> new IllegalArgumentException("Недействительный ключ лицензии"));

        if (!license.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Вы не являетесь владельцем данной лицензии");
        }

        if (license.getFirstActivationDate() == null) {
            throw new IllegalArgumentException("Дата активации не установлена");
        }

        if (license.getBlocked() || license.getEndingDate().isBefore(LocalDate.now())) {
            String deviceIds = getDeviceIdsForLicense(license);
            return generateTicket(license, deviceIds, "Лицензия заблокирована или истекла");
        }

        if (license.getEndingDate() == null || license.getEndingDate().isBefore(LocalDate.now())) {
            license.setEndingDate(license.getFirstActivationDate().plusDays(license.getDuration()));
        } else {
            license.setEndingDate(license.getEndingDate().plusDays(30));
        }

        licenseRepository.save(license);

        String deviceIds = getDeviceIdsForLicense(license);
        return generateTicket(license, deviceIds, null);
    }

    public Ticket generateTicket(License license, String deviceIds, String reason) {
        if (reason != null && !reason.isEmpty()) {
            System.err.println("Причина: " + reason);
        }

        Ticket ticket = new Ticket(
                30L,
                license.getFirstActivationDate(),
                license.getEndingDate(),
                license.getOwnerId(),
                deviceIds,
                license.getBlocked()
        );

        ticket.updateDigitalSignature(loadPrivateKey());

        return ticket;
    }

    private PrivateKey loadPrivateKey() {
        try {
            return KeyLoader.loadPrivateKey();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке приватного ключа", e);
        }
    }

    public List<DeviceLicense> getAllDeviceLicensesByLicenseId(Long licenseId) {
        return deviceLicenseRepository.findAllByLicenseId(licenseId);
    }

    public Optional<License> findLicenseById(Long licenseId) {
        return licenseRepository.findById(licenseId);
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
        return deviceLicenseRepository.findDeviceLicenseByDevice(device).stream()
                .map(DeviceLicense::getLicense)
                .collect(Collectors.toList());
    }
}