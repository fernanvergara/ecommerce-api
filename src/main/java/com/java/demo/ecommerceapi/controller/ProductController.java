package com.java.demo.ecommerceapi.controller;

import com.java.demo.ecommerceapi.dto.ProductDTO;
import com.java.demo.ecommerceapi.exception.GeneralException;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Tag(name = "Products", description = "Endpoints for product management")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Obtener listado de todos los productos",
            description = "Devuelve la informacion de todos los productos registrados",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos encontrados",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Product.class))),
                    @ApiResponse(responseCode = "404", description = "Productos no encontrado")
            }
    )
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if(products.isEmpty()){
            return new ResponseEntity<>( HttpStatus.NOT_FOUND);
        }else{
            List<ProductDTO> listDTO = new ArrayList<>();
            for (Product product : products) {
                listDTO.add(new ProductDTO(product));
            }
            return new ResponseEntity<>(listDTO, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(new ProductDTO( product.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody Product product) {
        Product newProduct = productService.createProduct(product);
        if(newProduct == null){
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(new ProductDTO(newProduct), HttpStatus.CREATED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        if (updatedProduct != null) {
            return new ResponseEntity<>(new ProductDTO(updatedProduct), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> existingProduct = productService.getProductById(id);
        if(existingProduct.isPresent()){
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            return new ResponseEntity<>( HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam(defaultValue = "", required = false, name = "name") String name,
            @RequestParam(defaultValue = "", required = false, name = "category") String category,
            @RequestParam(defaultValue = "", required = false, name = "minPrice") BigDecimal minPrice,
            @RequestParam(defaultValue = "", required = false, name = "maxPrice") BigDecimal maxPrice) {

        try {
            List<Product> products = new ArrayList<>();
            if(name==null){ name = ""; }
            if(category==null){ category = ""; }
            if(minPrice==null){ minPrice=BigDecimal.ZERO; }
            if(maxPrice==null){ maxPrice=BigDecimal.ZERO; }

            final BigDecimal comp = BigDecimal.ZERO;
            if((name != null || !name.isEmpty()) && (category==null || category.isEmpty()) && ((minPrice.compareTo(comp))==0 ) && (maxPrice.compareTo(comp)==0 )){
                products = productService.searchProductsByName(name);
            }else if((name == null || name.isEmpty()) && (category!=null || !category.isEmpty()) && ((minPrice.compareTo(comp))==0 ) && (maxPrice.compareTo(comp)==0 )){
                products = productService.searchProductsByCategory(category);
            }else if((name == null || name.isEmpty()) && (category==null || category.isEmpty()) && ((minPrice.compareTo(comp))!=0 ) && (maxPrice.compareTo(comp)==0 )){
                products = productService.searchProductsByPriceMinimun(minPrice);
            }else if((name == null || name.isEmpty()) && (category==null || category.isEmpty()) && ((minPrice.compareTo(comp))==0 ) && (maxPrice.compareTo(comp)!=0 )){
                products = productService.searchProductsByPriceMaximun(maxPrice);
            }else if((name == null || name.isEmpty()) && (category==null || category.isEmpty()) && ((minPrice.compareTo(comp))!=0 ) && (maxPrice.compareTo(comp)!=0 )){
                products = productService.searchProductsByPriceRange(minPrice, maxPrice);
            }else{
                minPrice = ((minPrice.compareTo(comp))==0 )?null:minPrice ;
                maxPrice = ((maxPrice.compareTo(comp))==0 )?null:maxPrice ;
                products = productService.searchProducts(name, category, minPrice, maxPrice);
            }
            
            if(products.isEmpty()){
                return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
            }else{
                List<ProductDTO> listDTO = new ArrayList<>();
                for (Product product : products) {
                    listDTO.add(new ProductDTO(product));
                }
                return new ResponseEntity<>(listDTO, HttpStatus.OK);
            }
        } catch (Exception e) {
                throw new GeneralException("Error searching de products");
        }
    }
}
