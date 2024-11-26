package ru.mtuci.Dubovikov.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    private String password_hash;
    private String email;
    private ApplicationRole role;
    public ApplicationUser(Long id, String login, String password_hash) {
        this.id = id;
        this.login = login;
        this.password_hash = password_hash;
    }
}
