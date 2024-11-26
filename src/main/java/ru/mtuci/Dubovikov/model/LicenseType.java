package ru.mtuci.Dubovikov.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "license_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicenseType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "default_duration")
    private Integer defaultDuration;

    @Column(name = "description")
    private String description;
}
