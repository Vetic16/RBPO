package ru.mtuci.rbpo_2024_praktika.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

@Getter
@Setter
@AllArgsConstructor
public class Ticket {

    private LocalDateTime serverDate;
    private Long ticketLifetime;
    private LocalDate activationDate;
    private LocalDate expirationDate;
    private Long userId;
    private String deviceId;
    private boolean isLicenseBlocked;
    private String digitalSignature;

    private static final String ALGORITHM = "SHA256withRSA";

    public Ticket(Long ticketLifetime, LocalDate activationDate, LocalDate expirationDate, Long userId, String deviceIds, boolean isLicenseBlocked) {
        this.serverDate = LocalDateTime.now();
        this.ticketLifetime = ticketLifetime;
        this.activationDate = activationDate;
        this.expirationDate = expirationDate;
        this.userId = userId;
        this.deviceId = deviceIds;
        this.isLicenseBlocked = isLicenseBlocked;
        this.digitalSignature = generateDigitalSignature();
    }

    private String generateDigitalSignature() {
        try {
            String data = String.format(
                    "serverDate:%s|ticketLifetime:%s|activationDate:%s|expirationDate:%s|userId:%s|deviceId:%s|isLicenseBlocked:%s",
                    serverDate, ticketLifetime, activationDate, expirationDate, userId, deviceId, isLicenseBlocked
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            PrivateKey privateKey = KeyLoader.loadPrivateKey();

            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initSign(privateKey);
            signature.update(hash);

            byte[] signedData = signature.sign();

            return Base64.getEncoder().encodeToString(signedData);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании цифровой подписи", e);
        }
    }

    public void updateDigitalSignature(PrivateKey privateKey) {
        try {
            String data = String.format(
                    "serverDate:%s|ticketLifetime:%s|activationDate:%s|expirationDate:%s|userId:%s|deviceId:%s|isLicenseBlocked:%s",
                    serverDate, ticketLifetime, activationDate, expirationDate, userId, deviceId, isLicenseBlocked
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(hash);

            byte[] signedData = signature.sign();

            this.digitalSignature = Base64.getEncoder().encodeToString(signedData);

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            System.err.println("Ошибка при обновлении цифровой подписи: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка при обновлении цифровой подписи", e);
        }
    }


    public boolean verifyDigitalSignature() {
        try {
            String data = String.format(
                    "serverDate:%s|ticketLifetime:%s|activationDate:%s|expirationDate:%s|userId:%s|deviceId:%s|isLicenseBlocked:%s",
                    serverDate, ticketLifetime, activationDate, expirationDate, userId, deviceId, isLicenseBlocked
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            PublicKey publicKey = KeyLoader.loadPublicKey();

            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(hash);

            return signature.verify(Base64.getDecoder().decode(this.digitalSignature));

        } catch (Exception e) {
            throw new RuntimeException("Ошибка проверки цифровой подписи", e);
        }
    }
}
