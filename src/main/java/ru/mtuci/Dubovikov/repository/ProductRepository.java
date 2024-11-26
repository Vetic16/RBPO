package ru.mtuci.Dubovikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.Dubovikov.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Можно добавить кастомные методы, если нужно
}
