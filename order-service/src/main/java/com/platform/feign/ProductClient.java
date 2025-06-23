package com.platform.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import com.platform.model.ProductDTO; // Import the ProductDTO class

@FeignClient(name = "ecom-product-service")
public interface ProductClient {
    @GetMapping("/products/{productId}")
    ProductDTO getProductById(@PathVariable Long productId);

    @PutMapping("/products/updateStockQuantity/{productId}")
    void updateProduct(@PathVariable Long productId, @RequestParam("quantity") int quantity);
}
// @Component
// public class ProductClientFallback implements ProductClient {
//     @Override
//     public ProductDTO getProductById(Long productId) {
//         return null; // Return a default or null value
//     }

//     @Override
//     public void updateProduct(Long productId, int quantity) {
//         System.out.println("Fallback: Unable to update product stock");
//     }
// }