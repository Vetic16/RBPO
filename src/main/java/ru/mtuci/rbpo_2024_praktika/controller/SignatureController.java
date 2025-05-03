package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.Signature;
import ru.mtuci.rbpo_2024_praktika.model.SignatureAudit;
import ru.mtuci.rbpo_2024_praktika.service.ApplicationUserService;
import ru.mtuci.rbpo_2024_praktika.service.SignatureService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor  // Lombok генерирует конструктор для всех final полей
@RequestMapping("/signatures")
public class SignatureController {

    private final SignatureService signatureService;  // final поле
    private final ApplicationUserService applicationUserService;  // final поле

    // Получение всех актуальных сигнатур
    @GetMapping("/all")
    public List<Signature> getAllSignatures() {
        return signatureService.getAllSignatures();
    }

    // Получение актуальных сигнатур, обновленных после заданного времени
    @GetMapping("/diff")
    public List<Signature> getUpdatedSignatures(@RequestParam("since") String since) {
        try {
            LocalDateTime parsedSince = LocalDateTime.parse(since, DateTimeFormatter.ISO_DATE_TIME);
            return signatureService.getUpdatedSignatures(parsedSince);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format, please use ISO date format.");
        }
    }

    // Получение сигнатур по списку GUID
    @PostMapping("/by-guids")
    public List<Signature> getSignaturesByGuids(@RequestBody List<UUID> guids) {
        return signatureService.getSignaturesByGuids(guids, Signature.SignatureStatus.ACTUAL.name());
    }

    // Создание новой сигнатуры
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Signature createSignature(@RequestBody Signature signatureRequest) {
        ApplicationUser currentUser = getCurrentUser(); // Получаем текущего пользователя
        return signatureService.createSignature(signatureRequest, currentUser);
    }

    private ApplicationUser getCurrentUser() {
        // Получаем текущего пользователя из SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return applicationUserService.findByEmail(username); // Или findUserByUsername
    }


    // Метод для "удаления" сигнатуры (фактическое обновление статуса на DELETED)
    @DeleteMapping("/signatures/delete/{id}")
    public ResponseEntity<Void> deleteSignature(@PathVariable UUID id, @RequestParam String changedBy) {
        signatureService.deleteSignature(id, changedBy);
        return ResponseEntity.noContent().build(); // Статус 204 No Content
    }

    // Обновление сигнатуры
    @PutMapping("/update/{id}")
    public Signature updateSignature(@PathVariable UUID id, @RequestBody Signature signatureRequest, @RequestParam String changedBy) {
        return signatureService.updateSignature(id, signatureRequest, changedBy);
    }

    // Получение записей по статусу
    @GetMapping("/status/{status}")
    public List<Signature> getSignaturesByStatus(@PathVariable String status) {
        return signatureService.getSignaturesByStatus(status); // Возвращаем сигнатуры по статусу
    }

    // Получение всех записей аудита
    @GetMapping("/audit")
    public List<SignatureAudit> getAllAuditRecords() {
        return signatureService.getAllAuditRecords(); // Возвращаем все записи аудита
    }

    // Получение записей аудита по ID сигнатуры
    @GetMapping("/audit/{signatureId}")
    public List<SignatureAudit> getAuditRecordsBySignature(@PathVariable UUID signatureId) {
        return signatureService.getAuditRecordsBySignature(signatureId); // Возвращаем записи аудита по ID сигнатуры
    }
}
