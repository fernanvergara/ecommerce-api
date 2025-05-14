package com.java.demo.ecommerceapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un producto")
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Schema(description = "Identificador único del producto", example = "123")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop")
    @Column(nullable = false)
    private String name;

    @Schema(description = "Descripcion del producto", example = "Dispositivo electonico portatil")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Schema(description = "Precio del producto", example = "999.99")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Schema(description = "Marca del producto", example = "RC")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Schema(description = "Categoria del producto", example = "Electronics")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Schema(description = "Stocks del producto", example = "")
    @OneToMany(mappedBy = "product")
    private List<Stock> stocks;

    @Schema(description = "Relacion del producto en detalles de ordenes", example = "")
    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;
}
