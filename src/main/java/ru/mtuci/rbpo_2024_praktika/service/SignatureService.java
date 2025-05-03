package ru.mtuci.rbpo_2024_praktika.service;

import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.*;
import ru.mtuci.rbpo_2024_praktika.repository.SignatureAuditRepository;
import ru.mtuci.rbpo_2024_praktika.repository.SignatureHistoryRepository;
import ru.mtuci.rbpo_2024_praktika.repository.SignatureRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SignatureService {

    private final SignatureRepository signatureRepository;
    private final SignatureHistoryRepository signatureHistoryRepository;
    private final SignatureAuditRepository signatureAuditRepository;
    private final ApplicationUserService applicationUserService;

    public SignatureService(SignatureRepository signatureRepository,
                            SignatureHistoryRepository signatureHistoryRepository,
                            SignatureAuditRepository signatureAuditRepository,
                            ApplicationUserService applicationUserService) {
        this.signatureRepository = signatureRepository;
        this.signatureHistoryRepository = signatureHistoryRepository;
        this.signatureAuditRepository = signatureAuditRepository;
        this.applicationUserService = applicationUserService;
    }

    public List<Signature> getAllSignatures() {
        return signatureRepository.findByStatusNot(Signature.SignatureStatus.DELETED);
    }

    public List<Signature> getSignaturesByStatus(String statusStr) {
        // Преобразуем строку в enum
        Signature.SignatureStatus status = Signature.SignatureStatus.valueOf(statusStr);
        return signatureRepository.findByStatus(status);  // Используем enum
    }

    public List<Signature> getUpdatedSignatures(LocalDateTime since) {
        return signatureRepository.findByUpdatedAtAfterAndStatus(since, Signature.SignatureStatus.ACTUAL);
    }

    public List<Signature> getSignaturesByIds(List<UUID> ids) {
        return signatureRepository.findByIdIn(ids);
    }

    public List<Signature> getSignaturesByGuids(List<UUID> guids, String statusStr) {
        Signature.SignatureStatus status = Signature.SignatureStatus.valueOf(statusStr);
        return signatureRepository.findByIdInAndStatus(guids, status);
    }

    public Signature createSignature(Signature signatureRequest, ApplicationUser changedBy) {
        signatureRequest.setUpdatedAt(new Date());
        signatureRequest.setStatus(Signature.SignatureStatus.ACTUAL);
        signatureRequest.setVersion(1);
        Signature saved = signatureRepository.save(signatureRequest);

        createAudit(saved, AuditChangeType.CREATED, changedBy);
        return saved;
    }

    public Signature updateSignature(UUID id, Signature newSignature, String username) {
        Signature existing = signatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сигнатура не найдена"));

        ApplicationUser user = applicationUserService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        saveHistory(existing, user);

        existing.setThreatName(newSignature.getThreatName());
        existing.setFirstBytes(newSignature.getFirstBytes());
        existing.setRemainderHash(newSignature.getRemainderHash());
        existing.setRemainderLength(newSignature.getRemainderLength());
        existing.setFileType(newSignature.getFileType());
        existing.setOffsetStart(newSignature.getOffsetStart());
        existing.setOffsetEnd(newSignature.getOffsetEnd());
        existing.setDigitalSignature(newSignature.getDigitalSignature());
        existing.setStatus(newSignature.getStatus());
        existing.setUpdatedAt(new Date());
        existing.setVersion(existing.getVersion() + 1);

        Signature updated = signatureRepository.save(existing);
        createAudit(updated, AuditChangeType.UPDATED, user);
        return updated;
    }

    public void deleteSignature(UUID id, String username) {
        Signature sig = signatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сигнатура не найдена"));

        ApplicationUser user = applicationUserService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        saveHistory(sig, user);
        sig.setStatus(Signature.SignatureStatus.DELETED);
        sig.setUpdatedAt(new Date());
        signatureRepository.save(sig);
        createAudit(sig, AuditChangeType.DELETED, user);
    }

    public void markCorrupted(Signature sig, String reason) {
        ApplicationUser systemUser = applicationUserService.findUserByUsername("SYSTEM")
                .orElseThrow(() -> new RuntimeException("SYSTEM user not found"));

        saveHistory(sig, systemUser);
        sig.setStatus(Signature.SignatureStatus.CORRUPTED);
        sig.setUpdatedAt(new Date());
        signatureRepository.save(sig);
        createAudit(sig, AuditChangeType.CORRUPTED, systemUser);
    }

    public List<SignatureHistory> getHistoryBySignatureId(UUID signatureId) {
        return signatureHistoryRepository.findBySignatureId(signatureId);
    }

    public List<SignatureAudit> getAllAuditRecords() {
        return signatureAuditRepository.findAll();
    }

    public List<SignatureAudit> getAuditRecordsBySignature(UUID signatureId) {
        return signatureAuditRepository.findBySignatureId(signatureId);
    }

    private void saveHistory(Signature sig, ApplicationUser modifiedBy) {

        SignatureHistory history = new SignatureHistory();
        history.setSignature(sig);
        history.setVersion(sig.getVersion());
        history.setThreatName(sig.getThreatName());
        history.setFirstBytes(sig.getFirstBytes());
        history.setRemainderHash(sig.getRemainderHash());
        history.setRemainderLength(sig.getRemainderLength());
        history.setFileType(sig.getFileType());
        history.setOffsetStart(sig.getOffsetStart());
        history.setOffsetEnd(sig.getOffsetEnd());
        history.setDigitalSignature(sig.getDigitalSignature());
        history.setStatus(sig.getStatus());
        history.setModifiedBy(modifiedBy); // Устанавливаем объект ApplicationUser
        history.setUpdatedAt(sig.getUpdatedAt());
        signatureHistoryRepository.save(history);
    }

    private void createAudit(Signature sig, AuditChangeType type, ApplicationUser changedBy) {
        SignatureAudit audit = new SignatureAudit();
        audit.setSignature(sig);
        audit.setChangedAt(new Date());
        audit.setChangedBy(changedBy); // Передаем объект ApplicationUser
        audit.setChangeType(type); // Передаем объект AuditChangeType напрямую
        audit.setFieldsChanged("Все поля или ключевые изменения"); // Можно уточнить, если нужно
        signatureAuditRepository.save(audit);
    }

    public List<Signature> getAllActualSignatures() {
        return signatureRepository.findByStatus(Signature.SignatureStatus.ACTUAL);
    }
}
