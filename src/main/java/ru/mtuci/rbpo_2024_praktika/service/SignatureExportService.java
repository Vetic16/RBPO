package ru.mtuci.rbpo_2024_praktika.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.Signature;
import ru.mtuci.rbpo_2024_praktika.repository.SignatureRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignatureExportService {

    private final SignatureRepository signatureRepository;

    @Transactional
    public void writeMultipartResponse(OutputStream out, String boundary) {
        try {
            List<Signature> signatures = signatureRepository.findAll();

            // Часть 1: бинарные сигнатуры
            byte[] signatureBytes = generateSignatureBytes(signatures);
            writePart(out, boundary, "signatures.bin", "application/octet-stream", signatureBytes);

            // Часть 2: текстовый манифест
            byte[] manifestBytes = generateManifest(signatures);
            writePart(out, boundary, "manifest.txt", "text/plain; charset=UTF-8", manifestBytes);

            // Завершение
            out.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
            out.flush();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при формировании multipart ответа", e);
        }
    }

    private void writePart(OutputStream out, String boundary, String filename, String contentType, byte[] data) throws IOException {
        String headers = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + filename + "\"; filename=\"" + filename + "\"\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + data.length + "\r\n\r\n";
        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(data);
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private byte[] generateSignatureBytes(List<Signature> signatures) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {

            for (Signature sig : signatures) {
                // UUID: 16 байт
                dos.writeLong(sig.getId().getMostSignificantBits());
                dos.writeLong(sig.getId().getLeastSignificantBits());

                // threat_name
                writeString(dos, sig.getThreatName());

                // first_bytes
                writeByteArray(dos, sig.getFirstBytes());

                // remainder_hash (строка hex → байты)
                byte[] remainderHashBytes = hexStringToByteArray(sig.getRemainderHash());
                writeByteArray(dos, remainderHashBytes);

                // remainder_length
                dos.writeInt(sig.getRemainderLength());

                // file_type
                writeString(dos, sig.getFileType());

                // offset_start
                dos.writeInt(sig.getOffsetStart());

                // offset_end
                dos.writeInt(sig.getOffsetEnd());
            }

            return bos.toByteArray();
        }
    }

    private void writeString(DataOutputStream dos, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(bytes.length); // длина строки
        dos.write(bytes);           // байты строки
    }

    private void writeByteArray(DataOutputStream dos, byte[] data) throws IOException {
        dos.writeInt(data.length);  // длина массива
        dos.write(data);            // содержимое
    }

    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private byte[] generateManifest(List<Signature> signatures) {
        StringBuilder sb = new StringBuilder("Signature Manifest\n\n");
        for (Signature sig : signatures) {
            sb.append("ID: ").append(sig.getId()).append("\n");
            sb.append("Threat: ").append(sig.getThreatName()).append("\n");
            sb.append("Updated: ").append(sig.getUpdatedAt()).append("\n\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
