package com.platform.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.platform.entity.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query method to find products by category   
    List<Product> findByCategory(String category);
}
