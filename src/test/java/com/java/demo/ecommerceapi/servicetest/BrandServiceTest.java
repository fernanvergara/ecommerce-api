package com.java.demo.ecommerceapi.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.java.demo.ecommerceapi.exception.ObjectNotFoundException;
import com.java.demo.ecommerceapi.model.Brand;
import com.java.demo.ecommerceapi.repository.BrandRepository;
import com.java.demo.ecommerceapi.service.BrandService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllBrands() {
        // Arrange
        List<Brand> brands = new ArrayList<>();
        brands.add(new Brand(1L, "Brand A", null));
        brands.add(new Brand(2L, "Brand B", null));
        when(brandRepository.findAll()).thenReturn(brands);

        // Act
        List<Brand> result = brandService.getAllBrands();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Brand A", result.get(0).getName());
        assertEquals("Brand B", result.get(1).getName());
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    public void testGetBrandById() {
        // Arrange
        Long brandId = 100L;
        Brand brand = new Brand(brandId, "Brand X", null);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        // Act
        Optional<Brand> result = brandService.getBrandById(brandId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(brandId, result.get().getId());
        assertEquals("Brand X", result.get().getName());
        verify(brandRepository, times(1)).findById(brandId);
    }

    @Test
    public void testCreateBrand() {
        // Arrange
        Brand brandToSave = new Brand(null, "Brand Y", null);
        Brand savedBrand = new Brand(1L, "Brand Y", null);
        when(brandRepository.save(brandToSave)).thenReturn(savedBrand);

        // Act
        Brand result = brandService.createBrand(brandToSave);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Brand Y", result.getName());
        verify(brandRepository, times(1)).save(brandToSave);
    }

    @Test
    public void testUpdateBrand() {
        // Arrange
        Long brandId = 100L;
        Brand existingBrand = new Brand(brandId, "Brand Z", null);
        Brand updatedBrand = new Brand(brandId, "Brand Z Updated", null);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(existingBrand));
        when(brandRepository.save(updatedBrand)).thenReturn(updatedBrand);

        // Act
        Brand result = brandService.updateBrand(brandId, updatedBrand);

        // Assert
        assertEquals(brandId, result.getId());
        assertEquals("Brand Z Updated", result.getName());
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, times(1)).save(updatedBrand);
    }

    @Test
    public void testUpdateBrand_BrandNotFound() {
        // Arrange
        Long brandId = 100L;
        Brand updatedBrand = new Brand(brandId, "Brand Z Updated", null);
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            brandService.updateBrand(brandId, updatedBrand);
        });
        assertEquals("Brand 'Brand Z Updated' not found with id:100", exception.getMessage());
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, never()).save(updatedBrand);
    }

    @Test
    public void testDeleteBrand() {
        // Arrange
        Long brandId = 100L;
        Brand existingBrand = new Brand(brandId, "Brand Z", null);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(existingBrand));
        doNothing().when(brandRepository).deleteById(brandId);

        // Act
        brandService.deleteBrand(brandId);

        // Assert
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, times(1)).deleteById(brandId);
    }

    @Test
    public void testDeleteBrand_BrandNotFound() {
        // Arrange
        Long brandId = 100L;
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            brandService.deleteBrand(brandId);
        });
        assertEquals("Brand not found with id:100", exception.getMessage());
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, never()).deleteById(brandId);
    }
}