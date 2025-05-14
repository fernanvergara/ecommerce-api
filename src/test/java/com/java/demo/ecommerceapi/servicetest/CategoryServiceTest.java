package com.java.demo.ecommerceapi.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.java.demo.ecommerceapi.exception.ObjectNotFoundException;
import com.java.demo.ecommerceapi.model.Category;
import com.java.demo.ecommerceapi.repository.CategoryRepository;
import com.java.demo.ecommerceapi.service.CategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCategories() {
        // Arrange
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "Category A", null));
        categories.add(new Category(2L, "Category B", null));
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Category A", result.get(0).getName());
        assertEquals("Category B", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void testGetCategoryById() {
        // Arrange
        Long categoryId = 100L;
        Category category = new Category(categoryId, "Category X", null);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = categoryService.getCategoryById(categoryId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(categoryId, result.get().getId());
        assertEquals("Category X", result.get().getName());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    public void testCreateCategory() {
        // Arrange
        Category categoryToSave = new Category(null, "Category Y", null);
        Category savedCategory = new Category(1L, "Category Y", null);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);

        // Act
        Category result = categoryService.createCategory(categoryToSave);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Category Y", result.getName());
        verify(categoryRepository, times(1)).save(categoryToSave);
    }

    @Test
    public void testUpdateCategory() {
        // Arrange
        Long categoryId = 100L;
        Category existingCategory = new Category(categoryId, "Category Z", null);
        Category updatedCategory = new Category(categoryId, "Category Z Updated", null);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        // Act
        Category result = categoryService.updateCategory(categoryId, updatedCategory);

        // Assert
        assertEquals(categoryId, result.getId());
        assertEquals("Category Z Updated", result.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(updatedCategory);
    }

    @Test
    public void testUpdateCategory_CategoryNotFound() {
        // Arrange
        Long categoryId = 100L;
        Category updatedCategory = new Category(categoryId, "Category Z Updated", null);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            categoryService.updateCategory(categoryId, updatedCategory);
        });
        assertEquals("Category 'Category Z Updated' not found with id:100", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).save(updatedCategory);
    }

    @Test
    public void testDeleteCategory() {
        // Arrange
        Long categoryId = 100L;
        Category existingCategory = new Category(categoryId, "Category Z", null);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        // Act
        categoryService.deleteCategory(categoryId);

        // Assert
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    public void testDeleteCategory_CategoryNotFound() {
        // Arrange
        Long categoryId = 100L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });
        assertEquals("Category not found with id:100", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).deleteById(categoryId);
    }
}

