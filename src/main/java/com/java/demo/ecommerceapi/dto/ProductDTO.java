package com.java.demo.ecommerceapi.dto;

import java.math.BigDecimal;

import com.java.demo.ecommerceapi.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long brandId;
    private Long categoryId;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.brandId = product.getBrand().getId();
        this.categoryId = product.getCategory().getId();
    }

    
}
