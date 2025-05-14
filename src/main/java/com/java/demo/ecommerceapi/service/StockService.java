/**
 * Nota:
 * Estoy omitiendo el uso de la interfaz para los servicios
 */
package com.java.demo.ecommerceapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.demo.ecommerceapi.exception.ObjectNotFoundException;
import com.java.demo.ecommerceapi.model.Stock;
import com.java.demo.ecommerceapi.repository.StockRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {
    private final StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockById(Long id) {
        Optional<Stock> existingStock = stockRepository.findById(id);
        if (existingStock.isPresent()) {
            return existingStock;
        } else {
             throw new ObjectNotFoundException("Stock not found with id:" + id);
        }
    }

    public Stock createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock updateStock(Long id, Stock stock) {
        Optional<Stock> existingStock = stockRepository.findById(id);
        if (existingStock.isPresent()) {
            Stock updatedStock = existingStock.get();
            updatedStock.setProduct(stock.getProduct());
            updatedStock.setQuantity(stock.getQuantity());
            updatedStock.setLocation(stock.getLocation());
            return stockRepository.save(updatedStock);
        } else {
             throw new ObjectNotFoundException("Stock of '"+stock.getProduct().getName()+"' not found with id:" + id);
        }
    }

    public void deleteStock(Long id) {
        Optional<Stock> stock = stockRepository.findById(id); 
        if (stock.isPresent()) {
            stockRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundException("Stock not found with id:" + id);
        }
    }

    public Optional<Stock> getStockByProductIdAndLocation(Long productId, String location) {
        return stockRepository.findByProductIdAndLocation(productId, location);
    }

    public List<Stock> getStocksByProductId(Long productId) {
        return stockRepository.findByProductId(productId);
    }

    @Transactional
    public void updateStockQuantity(Long productId, String location, int quantity) {
        Optional<Stock> stock = stockRepository.findByProductIdAndLocation(productId, location);
        if (stock.isPresent()) {
            Stock stockToUpdate = stock.get();
            stockToUpdate.setQuantity(quantity);
            stockRepository.save(stockToUpdate);
        } else {
            throw new ObjectNotFoundException("Stock not found for product id:"+productId+" and location:"+location);
        }
    }
}
