package com.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.entity.Product;
import com.platform.model.ProductDTO;
import com.platform.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired  
    private ProductService productService;

    // Public: anyone can view all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Public: anyone can view a product by id
    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setStockQuantity(product.getStockQuantity());
        return productDTO;
    }

    // Only ADMIN can add a product
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Product saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    // Only ADMIN can delete a product
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Only ADMIN can update a product
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Public: anyone can view products by category
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }

    // Only ADMIN can update stock quantity
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateStockQuantity/{productId}")
    public void updateStockQuantity(@PathVariable Long productId, @RequestParam int quantity) {
        productService.updateStockQuantity(productId, quantity);
    }
}
