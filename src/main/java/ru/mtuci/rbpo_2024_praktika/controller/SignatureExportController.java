package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.rbpo_2024_praktika.model.Signature;
import ru.mtuci.rbpo_2024_praktika.service.ManifestSigner;
import ru.mtuci.rbpo_2024_praktika.service.SignatureService;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signatures")
public class SignatureExportController {

    private final SignatureService signatureService;
    private final ManifestSigner manifestSigner;

    @GetMapping(value = "/export", produces = MediaType.MULTIPART_MIXED_VALUE)
    public ResponseEntity<MultiValueMap<String, Object>> exportSignatures() throws IOException {
        List<Signature> signatures = signatureService.getAllActualSignatures();

        // Генерация файла signatures.dat
        byte[] signaturesData = generateSignatureData(signatures);

        // Генерация бинарного манифеста
        byte[] manifestBytes = generateManifestBinary(signatures);

        // Формируем multipart ответ
        ByteArrayResource signatureResource = new ByteArrayResource(signaturesData);
        ByteArrayResource manifestResource = new ByteArrayResource(manifestBytes);

        HttpHeaders sigHeaders = new HttpHeaders();
        sigHeaders.setContentDisposition(ContentDisposition.attachment().filename("signatures.dat").build());

        HttpHeaders manifestHeaders = new HttpHeaders();
        manifestHeaders.setContentDisposition(ContentDisposition.attachment().filename("manifest.bin").build());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("signatures", new HttpEntity<>(signatureResource, sigHeaders));
        body.add("manifest", new HttpEntity<>(manifestResource, manifestHeaders));

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    private byte[] generateSignatureData(List<Signature> signatures) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        for (Signature sig : signatures) {
            // Сначала пишем ID как строку
            dos.writeUTF(sig.getId().toString());

            byte[] digitalSignatureBytes = sig.getDigitalSignature();  // Прямо используем байты
            dos.writeInt(digitalSignatureBytes.length); // Пишем длину цифровой подписи
            dos.write(digitalSignatureBytes); // Пишем сами байты цифровой подписи
        }

        dos.flush();
        return baos.toByteArray();
    }

    private byte[] generateManifestBinary(List<Signature> signatures) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Генерация манифеста в бинарном формате
        dos.writeInt(signatures.size());  // Пишем количество записей

        for (Signature sig : signatures) {
            // Пишем ID как байты
            dos.writeLong(sig.getId().getMostSignificantBits());
            dos.writeLong(sig.getId().getLeastSignificantBits());

            // Пишем цифровую подпись как байты
            byte[] digitalSignatureBytes = sig.getDigitalSignature();
            dos.writeInt(digitalSignatureBytes.length); // Пишем длину
            dos.write(digitalSignatureBytes); // Пишем сами байты
        }

        // Подпись манифеста
        byte[] manifestContentBytes = baos.toByteArray();  // Получаем данные манифеста в бинарном виде
        String digitalSignature = manifestSigner.signManifest(manifestContentBytes);  // Подписываем данные
        byte[] signatureBytes = digitalSignature.getBytes(StandardCharsets.UTF_8);

        // Записываем подпись манифеста
        dos.writeInt(signatureBytes.length);  // Длина подписи
        dos.write(signatureBytes);  // Подпись

        dos.flush();
        return baos.toByteArray();
    }
}
