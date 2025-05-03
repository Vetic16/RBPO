package ru.mtuci.rbpo_2024_praktika.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.*;
import ru.mtuci.rbpo_2024_praktika.repository.DeviceLicenseRepository;
import ru.mtuci.rbpo_2024_praktika.repository.LicenseRepository;
import ru.mtuci.rbpo_2024_praktika.utils.ActivationCodeGenerator;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.Signature;
import java.time.LocalDate;
import java.util.Base64;
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

    private final KeyPair keyPair; // Пара ключей

    public Ticket activateLicense(String activationCode, Device device, ApplicationUser user) {
        License license = licenseRepository.findByCode(activationCode)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия не найдена"));

        if (!verifyLicenseSignature(license)) {
            throw new IllegalArgumentException("Цифровая подпись лицензии недействительна!");
        }

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

    private void updateLicense(License license, int remainingDevices) {
        license.setMaxDeviceCount(remainingDevices);
        licenseRepository.save(license);
    }

    private String getDeviceIdsForLicense(License license) {
        List<DeviceLicense> deviceLicenses = deviceLicenseRepository.findAllByLicenseId(license.getId());

        return deviceLicenses.stream()
                .map(deviceLicense -> deviceLicense.getDevice().getId().toString())
                .collect(Collectors.joining(","));
    }

    public Ticket generateTicket(License license, String deviceIds, String reason) {
        if (reason != null && !reason.isEmpty()) {
            System.err.println("Причина: " + reason);
        }

        return new Ticket(
                30L,
                license.getFirstActivationDate(),
                license.getEndingDate(),
                license.getOwnerId(),
                deviceIds,
                license.getBlocked()
        );
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

    public License createLicense(String code, Long licenseTypeId, Long userId, int maxDeviceCount, int duration, Long productId) {
        if (licenseRepository.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("Лицензия с таким кодом уже существует");
        }

        LicenseType licenseType = licenseTypeService.getLicenseTypeById(licenseTypeId);
        if (licenseType == null) {
            throw new IllegalArgumentException("Тип лицензии не найден");
        }

        ApplicationUser user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Продукт не найден");
        }

        License license = new License();
        license.setCode(code);
        license.setType(licenseType);
        license.setOwner(user);
        license.setMaxDeviceCount(maxDeviceCount);
        license.setDuration(duration);
        license.setBlocked(false);
        license.setFirstActivationDate(null);
        license.setDeviceCount(0);
        license.setEndingDate(null);
        license.setProduct(product);  // Устанавливаем product

        // Подписываем данные
        String signature = signLicenseData(license);
        license.setDigitalSignature(signature);

        licenseRepository.save(license);
        licenseHistoryService.recordLicenseChange(license, user, "Created", "Лицензия была создана");

        return license;
    }

    private String signLicenseData(License license) {
        try {
            String data = license.getCode() + "|" + license.getMaxDeviceCount() + "|" + license.getDuration();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(hash);
            byte[] digitalSignature = signature.sign();

            return Base64.getEncoder().encodeToString(digitalSignature);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при подписи лицензии", e);
        }
    }

    private boolean verifyLicenseSignature(License license) {
        try {
            String data = license.getCode() + "|" + license.getMaxDeviceCount() + "|" + license.getDuration();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = Base64.getDecoder().decode(license.getDigitalSignature());

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(keyPair.getPublic());
            signature.update(hash);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            return false;
        }
    }

    public void updateStatusAndLog(License license, LicenseStatus newStatus, ApplicationUser actor, String changeType, String comment) {
        License oldCopy = new License();
        oldCopy.setCode(license.getCode());
        oldCopy.setOwner(license.getOwner());
        oldCopy.setType(license.getType());
        oldCopy.setProduct(license.getProduct());
        oldCopy.setBlocked(license.getBlocked());
        oldCopy.setDuration(license.getDuration());
        oldCopy.setMaxDeviceCount(license.getMaxDeviceCount());
        oldCopy.setDeviceCount(license.getDeviceCount());
        oldCopy.setEndingDate(license.getEndingDate());
        oldCopy.setFirstActivationDate(license.getFirstActivationDate());
        oldCopy.setDigitalSignature(license.getDigitalSignature());

        licenseHistoryService.recordLicenseChange(oldCopy, actor, changeType, comment);
        licenseRepository.save(license);
    }
    public void deleteLicense(Long licenseId, ApplicationUser admin) {
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия не найдена"));

        updateStatusAndLog(license, LicenseStatus.DELETED, admin, "DELETE", "Лицензия была удалена администратором");
    }
    public void checkSignatureOrMarkCorrupted(License license, ApplicationUser user) {
        if (!verifyLicenseSignature(license)) {
            updateStatusAndLog(
                    license,
                    LicenseStatus.CORRUPTED,
                    user,
                    "CORRUPTED",
                    "Подпись недействительна, присвоен статус CORRUPTED"
            );
            throw new IllegalArgumentException("Цифровая подпись лицензии недействительна!");
        }
    }
}
