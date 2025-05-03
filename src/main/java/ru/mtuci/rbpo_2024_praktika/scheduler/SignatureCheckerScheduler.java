package ru.mtuci.rbpo_2024_praktika.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mtuci.rbpo_2024_praktika.service.SignatureCheckService;

@Component
@RequiredArgsConstructor
public class SignatureCheckerScheduler {

    private final SignatureCheckService signatureCheckService;

    // Периодическая задача для проверки ЭЦП (по умолчанию раз в сутки)
    @Scheduled(cron = ScheduleConfig.CRON_EXPRESSION)  // Используем константу из ScheduleConfig
    public void checkSignatures() {
        signatureCheckService.checkLicenses();
    }
}
