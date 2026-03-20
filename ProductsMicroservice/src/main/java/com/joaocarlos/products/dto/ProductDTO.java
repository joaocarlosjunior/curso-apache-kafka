package com.joaocarlos.products.dto;

import java.math.BigDecimal;

public record ProductDTO(
        String name,
        BigDecimal price,
        String description,
        Integer quantity
) {
}
