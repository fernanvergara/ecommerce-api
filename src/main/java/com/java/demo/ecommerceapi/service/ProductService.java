/**
 * Nota:
 * Estoy omitiendo el uso de la interfaz para los servicios
 */
package com.java.demo.ecommerceapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.demo.ecommerceapi.exception.ObjectNotFoundException;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        Optional<Product> existingProduct = productRepository.findByName(product.getName());
        if(existingProduct.isEmpty()){
            return productRepository.save(product);
        }else{
            throw new ObjectNotFoundException("Product '"+product.getName()+"' already exists with id:" + existingProduct.get().getId());
        }
    }

    @Transactional
    public Product updateProduct(Long id, Product product) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setName(product.getName());
            updatedProduct.setDescription(product.getDescription());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setBrand(product.getBrand());
            updatedProduct.setCategory(product.getCategory());
            return productRepository.save(updatedProduct);
        } else {
            throw new ObjectNotFoundException("Product '"+product.getName()+"' not found with id:" + id);
        }
    }

    public void deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id); 
        if (product.isPresent()) {
            productRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundException("Product not found with id:" + id);
        }
    }

    public List<Product> searchProducts(String name, String categoryName, BigDecimal minPrice, BigDecimal maxPrice) {
        if ((name == null || name.isEmpty()) && (categoryName == null || categoryName.isEmpty()) && minPrice == null && maxPrice == null) {
            return productRepository.findAll();
        }
        return productRepository.findByFilters(name, categoryName, minPrice, maxPrice);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> searchProductsByCategory(String categoryName) {
        return productRepository.findByCategoryNameIgnoreCase(categoryName);
    }

    public List<Product> searchProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
}
