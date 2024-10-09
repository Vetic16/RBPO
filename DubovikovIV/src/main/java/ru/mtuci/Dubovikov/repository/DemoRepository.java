package ru.mtuci.Dubovikov.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.Dubovikov.model.License;

@Repository
public interface DemoRepository extends JpaRepository<License, Long> {
    License findByName(String name);
}
