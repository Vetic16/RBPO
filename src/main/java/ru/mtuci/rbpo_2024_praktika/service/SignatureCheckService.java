package ru.mtuci.rbpo_2024_praktika.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo_2024_praktika.model.License;
import ru.mtuci.rbpo_2024_praktika.model.LicenseStatus;
import ru.mtuci.rbpo_2024_praktika.repository.LicenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignatureCheckService {

    private static final Logger logger = LoggerFactory.getLogger(SignatureCheckService.class);

    private final LicenseRepository licenseRepository;

    @Transactional
    public void checkLicenses() {
        // Получаем дату последней проверки, можно использовать текущее время
        LocalDateTime lastCheckDate = getLastCheckDate(); // Это может быть настроено через конфигурацию или сохраняться где-то

        // Получаем лицензии, обновленные с последней проверки
        List<License> licensesToCheck = licenseRepository.findLicensesWithUpdatesSinceLastCheck(lastCheckDate);

        // Проходим по каждой записи
        for (License license : licensesToCheck) {
            if (!verifyLicenseSignature(license)) {
                // Логируем ошибку
                logger.error("Ошибка в ЭЦП для лицензии с кодом: {}", license.getCode());

                licenseRepository.save(license);
            }
        }
    }

    // Метод для получения даты последней проверки, здесь можно использовать текущую дату или хранить дату в конфигурации
    private LocalDateTime getLastCheckDate() {
        // Для примера, используем текущее время
        return LocalDateTime.now().minusDays(1); // например, проверяем с прошлого дня
    }

    // Метод для проверки ЭЦП лицензии
    private boolean verifyLicenseSignature(License license) {
        // Здесь будет ваша логика для проверки подписи
        // Например, вычисление хеша и проверка подписи
        return true; // временный успех
    }
}
