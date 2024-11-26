package ru.mtuci.Dubovikov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.Dubovikov.model.Product;
import ru.mtuci.Dubovikov.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}
