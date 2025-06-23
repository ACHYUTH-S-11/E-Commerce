package com.platform.service;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import com.platform.repository.CartRepository;
import com.platform.entity.CartItem;
import com.platform.model.ProductDTO;
import com.platform.feign.ProductClient;
import java.util.Optional;
import java.util.List;
import com.platform.model.CartItemRequest;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductClient productClient;

    public List<CartItem> getCartItemsByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public CartItem addCartItem(CartItemRequest request) {
        Optional<CartItem> existingItem = cartRepository.findByUserIdAndProductId(request.getUserId(),
                request.getProductId());
        ProductDTO product = productClient.getProductById(request.getProductId());
        double pricePerUnit = product.getPrice();
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            item.setQuantity(newQuantity);
            item.setTotalPrice(newQuantity * pricePerUnit);
            return cartRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(request.getUserId());
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            newItem.setTotalPrice(request.getQuantity() * pricePerUnit);
            return cartRepository.save(newItem);
        }
    }

    public void removeCartItem(Long cartItemId) {
        Optional<CartItem> existingItem = cartRepository.findById(cartItemId);
        if (existingItem.isPresent()) {
            CartItem Item = existingItem.get();
            int currQuantity = Item.getQuantity();
            if (currQuantity > 1) {
                Item.setQuantity(currQuantity - 1);
                ProductDTO product = productClient.getProductById(Item.getProductId());
                double pricePerUnit = product.getPrice();
                Item.setTotalPrice((currQuantity - 1) * pricePerUnit);
                cartRepository.save(Item);
            } else {
                cartRepository.deleteById(cartItemId);
            }
        } else {
            throw new RuntimeException("Cart item not found with id: " + cartItemId);
        }
    }

    public double calculateTotalPrice(Long userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public void clearCart(Long userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        if (items.isEmpty()) {
            throw new RuntimeException("Cart is already empty for user with id: " + userId);
        }
        cartRepository.deleteAll(items);
    }
}
