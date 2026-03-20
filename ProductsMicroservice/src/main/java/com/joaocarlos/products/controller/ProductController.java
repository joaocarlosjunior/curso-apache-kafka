package com.joaocarlos.products.controller;

import com.joaocarlos.products.dto.ProductDTO;
import com.joaocarlos.products.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<String> createdProduct(@RequestBody ProductDTO product) {
        String productId = productService.create(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}
