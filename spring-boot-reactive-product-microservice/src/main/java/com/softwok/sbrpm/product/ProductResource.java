package com.softwok.sbrpm.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductResource {
    private final ProductRepository productRepository;

    @PostMapping
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping("/{id}")
    public Mono<Product> readProduct(@PathVariable("id") String id) {
        return productRepository.findById(id);
    }
}
