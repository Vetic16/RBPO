package ru.mtuci.rbpo_2024_praktika.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {
    private String refreshToken;
    private String deviceId;
}
