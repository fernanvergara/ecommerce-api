/**
 * Nota:
 * Estoy omitiendo el uso de la interfaz para los servicios
 */
package com.java.demo.ecommerceapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.java.demo.ecommerceapi.exception.ObjectNotFoundException;
import com.java.demo.ecommerceapi.model.Brand;
import com.java.demo.ecommerceapi.repository.BrandRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    public Brand getBrandByName(String name){
        Optional<Brand> existBrand = brandRepository.findByName(name);
        if(existBrand.isPresent()){
            return existBrand.get();
        }else{
            throw new ObjectNotFoundException("Brand '"+name+"' not found ");
        }
    }

    public Brand createBrand(Brand brand) {
        Optional<Brand> existingBrand = brandRepository.findByName(brand.getName());
        if(existingBrand.isEmpty()){
            return brandRepository.save(brand);
        }else{
            throw new ObjectNotFoundException("Brand '"+brand.getName()+"' already exists");
        }
    }

    @Transactional
    public Brand updateBrand(Long id, Brand brand) {
        Optional<Brand> existingBrand = brandRepository.findById(id);
        if (existingBrand.isPresent()) {
            Brand updatedBrand = existingBrand.get();
            updatedBrand.setName(brand.getName());
            return brandRepository.save(updatedBrand);
        } else {
            throw new ObjectNotFoundException("Brand '"+brand.getName()+"' not found with id:" + id);
        }
    }

public boolean deleteBrand(Long id) {
    Optional<Brand> brand = brandRepository.findById(id); 
    if (brand.isPresent()) {
        brandRepository.deleteById(id);
        return true;
    } else {
        throw new ObjectNotFoundException("Brand not found with id:" + id);
    }
}
}
