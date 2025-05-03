package ru.mtuci.rbpo_2024_praktika.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.utils.KeyLoader;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

@Slf4j
@Service
public class ManifestSigner {

    @Value("${keys.private}")
    private String privateKeyPath;

    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        try {
            privateKey = KeyLoader.loadPrivateKey(privateKeyPath);
            log.info("Приватный ключ успешно загружен из {}", privateKeyPath);
        } catch (Exception e) {
            log.error("Не удалось загрузить приватный ключ по пути: {}", privateKeyPath, e);
            throw new RuntimeException("Ошибка при загрузке приватного ключа", e);
        }
    }

    // Метод для подписания бинарных данных
    public String signManifest(byte[] data) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data);  // Прямое использование бинарных данных
            byte[] digitalSig = signature.sign();
            return Base64.getEncoder().encodeToString(digitalSig);  // Возвращаем подпись как строку
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при подписании манифеста", e);
        }
    }
}
