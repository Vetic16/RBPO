package ru.mtuci.DubovikovIV.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.DubovikovIV.model.ApplicationUser;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, UUID> {
    Optional<ApplicationUser> findByEmail(String email);
}
