/**
 * Nota:
 * Estoy omitiendo el uso de la interfaz para los servicios
 */
package com.java.demo.ecommerceapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.demo.ecommerceapi.exception.ObjectNotFoundException;
import com.java.demo.ecommerceapi.model.Category;
import com.java.demo.ecommerceapi.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category getCategoryByName(String name){
        return categoryRepository.findByName(name);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category category) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            Category updatedCategory = existingCategory.get();
            updatedCategory.setName(category.getName());
            return categoryRepository.save(updatedCategory);
        } else {
            throw new ObjectNotFoundException("Category '"+category.getName()+"' not found with id:" + id);
        }
    }

    public void deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id); 
        if (category.isPresent()) {
            categoryRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundException("Category not found with id:" + id);
        }
    }
}
