package ru.mtuci.rbpo_2024_praktika.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mac_address", unique = true, nullable = false)
    private String macAddress;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    public void setUserId(Long userId) {
        if (this.user == null) {
            this.user = new ApplicationUser();
        }
        this.user.setId(userId);
    }

    public Long getUserId() {
        return this.user != null ? this.user.getId() : null;
    }
}
