package com.java.demo.ecommerceapi.repository;

import com.java.demo.ecommerceapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryNameIgnoreCase(String categoryName);

    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice")
    List<Product> findByPriceMinimun(@Param("minPrice") BigDecimal minPrice);

    @Query("SELECT p FROM Product p WHERE p.price <= :maxPrice")
    List<Product> findByPriceMaximun(@Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE " +
           "(:name is null or lower(p.name) like lower(concat('%', :name, '%'))) and " +
           "(:categoryName is null or lower(p.category.name) = lower(:categoryName)) and " +
           "(:minPrice is null or p.price >= :minPrice) and " +
           "(:maxPrice is null or p.price <= :maxPrice) ORDER BY p.name ASC")
    List<Product> findByFilters(
            @Param("name") String name,
            @Param("categoryName") String categoryName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);
}
