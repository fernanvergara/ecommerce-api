package com.java.demo.ecommerceapi.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.java.demo.ecommerceapi.exception.ObjectNotFoundException;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.model.Stock;
import com.java.demo.ecommerceapi.repository.StockRepository;
import com.java.demo.ecommerceapi.service.StockService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllStocks() {
        // Arrange
        List<Stock> stocks = new ArrayList<>();
        stocks.add(new Stock());
        stocks.add(new Stock());
        when(stockRepository.findAll()).thenReturn(stocks);

        // Act
        List<Stock> result = stockService.getAllStocks();

        // Assert
        assertEquals(2, result.size());
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    public void testGetStockById() {
        // Arrange
        Long stockId = 100L;
        Stock stock = new Stock();
        stock.setId(stockId);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));

        // Act
        Optional<Stock> result = stockService.getStockById(stockId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(stockId, result.get().getId());
        verify(stockRepository, times(1)).findById(stockId);
    }

    @Test
    public void testCreateStock() {
        // Arrange
        Product product = new Product();
        Stock stockToSave = new Stock(null, product, 10, "Location A");
        Stock savedStock = new Stock(1L, product, 10, "Location A");
        when(stockRepository.save(stockToSave)).thenReturn(savedStock);

        // Act
        Stock result = stockService.createStock(stockToSave);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals(10, result.getQuantity());
        assertEquals("Location A", result.getLocation());
        verify(stockRepository, times(1)).save(stockToSave);
    }

    @Test
    public void testUpdateStock() {
        // Arrange
        Long stockId = 100L;
        Product product = new Product();
        Stock existingStock = new Stock(stockId, product, 10, "Location A");
        Stock updatedStock = new Stock(stockId, product, 15, "Location B");
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(updatedStock)).thenReturn(updatedStock);

        // Act
        Stock result = stockService.updateStock(stockId, updatedStock);

        // Assert
        assertEquals(stockId, result.getId());
        assertEquals(15, result.getQuantity());
        assertEquals("Location B", result.getLocation());
        verify(stockRepository, times(1)).findById(stockId);
        verify(stockRepository, times(1)).save(updatedStock);
    }

    @Test
    public void testUpdateStock_StockNotFound() {
        // Arrange
        Long stockId = 100L;
        Product product = new Product();
        Stock updatedStock = new Stock(stockId, product, 15, "Location B");
        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            stockService.updateStock(stockId, updatedStock);
        });
        assertEquals("Stock of 'null' not found with id:100", exception.getMessage());
        verify(stockRepository, times(1)).findById(stockId);
        verify(stockRepository, never()).save(updatedStock);
    }

    @Test
    public void testDeleteStock() {
        // Arrange
        Long stockId = 100L;
        Stock existingStock = new Stock();
        existingStock.setId(stockId);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(existingStock));

        // Act
        stockService.deleteStock(stockId);

        // Assert
        verify(stockRepository, times(1)).findById(stockId);
        verify(stockRepository, times(1)).deleteById(stockId);
    }

    @Test
    public void testDeleteStock_StockNotFound() {
        // Arrange
        Long stockId = 100L;
        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            stockService.deleteStock(stockId);
        });
        assertEquals("Stock not found with id:100", exception.getMessage());
        verify(stockRepository, times(1)).findById(stockId);
        verify(stockRepository, never()).deleteById(stockId);
    }

    @Test
    public void testGetStocksByProductId() {
        // Arrange
        Long productId = 50L;
        Product product = new Product();
        product.setId(productId);
        List<Stock> stocks = new ArrayList<>();
        stocks.add(new Stock(1L, product, 5, "Location X"));
        stocks.add(new Stock(2L, product, 10, "Location Y"));
        when(stockRepository.findByProductId(productId)).thenReturn(stocks);

        // Act
        List<Stock> result = stockService.getStocksByProductId(productId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getQuantity());
        assertEquals(10, result.get(1).getQuantity());
        verify(stockRepository, times(1)).findByProductId(productId);
    }

    @Test
    public void testUpdateStockQuantity() {
        // Arrange
        Long productId = 50L;
        String location = "Location Z";
        int quantity = 8;
        Stock existingStock = new Stock(1L, new Product(), 10, location);
        when(stockRepository.findByProductIdAndLocation(productId, location)).thenReturn(Optional.of(existingStock));

        // Act
        stockService.updateStockQuantity(productId, location, quantity);

        // Assert
        assertEquals(8, existingStock.getQuantity());
        verify(stockRepository, times(1)).findByProductIdAndLocation(productId, location);
        verify(stockRepository, times(1)).save(existingStock);
    }

    @Test
    public void testUpdateStockQuantity_StockNotFound() {
        // Arrange
        Long productId = 50L;
        String location = "Location Z";
        int quantity = 8;
        when(stockRepository.findByProductIdAndLocation(productId, location)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            stockService.updateStockQuantity(productId, location, quantity);
        });
        assertEquals("Stock not found for product id:50 and location:Location Z", exception.getMessage());
        verify(stockRepository, times(1)).findByProductIdAndLocation(productId, location);
        verify(stockRepository, never()).save(any(Stock.class));
    }
}
