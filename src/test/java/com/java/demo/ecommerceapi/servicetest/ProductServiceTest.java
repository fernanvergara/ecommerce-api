package com.java.demo.ecommerceapi.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.repository.ProductRepository;
import com.java.demo.ecommerceapi.service.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        // Arrange
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        products.add(new Product());
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testGetProductById() {
        // Arrange
        Long productId = 100L;
        Product product = new Product();
        product.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> result = productService.getProductById(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getId());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void testCreateProduct() {
        // Arrange
        Product productToSave = new Product(null, "Product 1", "Description 1", new BigDecimal("10.00"), null, null, null, null);
        Product savedProduct = new Product(1L, "Product 1", "Description 1", new BigDecimal("10.00"), null, null, null, null);
        when(productRepository.save(productToSave)).thenReturn(savedProduct);

        // Act
        Product result = productService.createProduct(productToSave);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Product 1", result.getName());
        verify(productRepository, times(1)).save(productToSave);
    }

    @Test
    public void testUpdateProduct() {
        // Arrange
        Long productId = 100L;
        Product existingProduct = new Product(productId, "Product 1", "Description 1", new BigDecimal("10.00"), null, null, null, null);
        Product updatedProduct = new Product(productId, "Product Updated", "Description Updated", new BigDecimal("15.00"), null, null, null, null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(productId, updatedProduct);

        // Assert
        assertEquals(productId, result.getId());
        assertEquals("Product Updated", result.getName());
        assertEquals("Description Updated", result.getDescription());
        assertEquals(new BigDecimal("15.00"), result.getPrice());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(updatedProduct);
    }

    @Test
    public void testUpdateProduct_ProductNotFound() {
        // Arrange
        Long productId = 100L;
        Product updatedProduct = new Product(productId, "Product Updated", "Description Updated", new BigDecimal("15.00"), null, null, null, null);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(productId, updatedProduct);
        });
        assertEquals("Product 'Product Updated' not found with id:100", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(updatedProduct);
    }

    @Test
    public void testDeleteProduct() {
        // Arrange
        Long productId = 100L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).deleteById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void testDeleteProduct_ProductNotFound() {
        // Arrange
        Long productId = 100L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(productId);
        });
        assertEquals("Product not found with id:100", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).deleteById(productId);
    }

    @Test
    public void testSearchProductsByName() {
        // Arrange
        String name = "Product";
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, "Product 1", "Description 1", new BigDecimal("10.00"), null, null, null, null));
        products.add(new Product(2L, "Product 2", "Description 2", new BigDecimal("20.00"), null, null, null, null));
        when(productRepository.findByNameContainingIgnoreCase(name)).thenReturn(products);

        // Act
        List<Product> result = productService.searchProductsByName(name);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Product 2", result.get(1).getName());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase(name);
        verify(productRepository, never()).findByCategoryNameIgnoreCase(anyString());
        verify(productRepository, never()).findByPriceBetween(any(), any());
    }

    @Test
    public void testSearchProductsByCategory() {
        // Arrange
        String categoryName = "Category A";
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, "Product 1", "Description 1", new BigDecimal("10.00"), null, null, null, null));
        products.add(new Product(2L, "Product 2", "Description 2", new BigDecimal("20.00"), null, null, null, null));
        when(productRepository.findByCategoryNameIgnoreCase(categoryName)).thenReturn(products);

        // Act
        List<Product> result = productService.searchProductsByCategory( categoryName);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Product 2", result.get(1).getName());
        verify(productRepository, never()).findByNameContainingIgnoreCase(anyString());
        verify(productRepository, times(1)).findByCategoryNameIgnoreCase(categoryName);
        verify(productRepository, never()).findByPriceBetween(any(), any());
    }

    @Test
    public void testSearchProductsByPriceRange() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("15.00");
        BigDecimal maxPrice = new BigDecimal("25.00");
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, "Product 1", "Description 1", new BigDecimal("20.00"), null, null, null, null));
        when(productRepository.findByPriceBetween(minPrice, maxPrice)).thenReturn(products);

        // Act
        List<Product> result = productService.searchProductsByPriceRange( minPrice, maxPrice);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals(new BigDecimal("20.00"), result.get(0).getPrice());
        verify(productRepository, never()).findByNameContainingIgnoreCase(anyString());
        verify(productRepository, never()).findByCategoryNameIgnoreCase(anyString());
        verify(productRepository, times(1)).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    public void testSearchProducts() {
        // Arrange
        String name = "Product";
        String categoryName = "Category A";
        BigDecimal minPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("20.00");
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, "Product 1", "Description 1", new BigDecimal("15.00"), null, null, null, null));
        when(productRepository.findByFilters(name, categoryName, minPrice, maxPrice)).thenReturn(products);

        // Act
        List<Product> result = productService.searchProducts(name, categoryName, minPrice, maxPrice);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Product 1", result.get(0).getName());
//        assertEquals("Category A", result.get(0).getCategory().getName()); // 
        assertEquals(new BigDecimal("15.00"), result.get(0).getPrice());
        verify(productRepository, times(1)).findByFilters(name, categoryName, minPrice, maxPrice);
        verify(productRepository, never()).findAll();
    }
}
