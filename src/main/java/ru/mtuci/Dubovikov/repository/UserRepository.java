package ru.mtuci.Dubovikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.Dubovikov.model.ApplicationUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByEmail(String email);

    Optional<ApplicationUser> findById(Long userId);
}

