package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbpo_2024_praktika.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsBlockedTrue();

    Optional<Product> findByName(String name);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:namePart%")
    List<Product> findByNameContaining(@Param("namePart") String namePart);

    @Modifying
    @Query("UPDATE Product p SET p.isBlocked = :isBlocked WHERE p.id = :id")
    void updateIsBlockedById(@Param("id") Long id, @Param("isBlocked") Boolean isBlocked);
}
