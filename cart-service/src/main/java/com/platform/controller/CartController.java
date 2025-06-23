package com.platform.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.service.CartService;
import com.platform.entity.CartItem;
import com.platform.model.CartItemRequest;

import java.util.List;

@RestController
@RequestMapping("/cart")

public class CartController {

    @Autowired
    private CartService cartService;    

    @GetMapping("/user/{userId}")
    public List<CartItem> getCartItemsByUserId(@PathVariable Long userId) {
        return cartService.getCartItemsByUserId(userId);
    }
    @PostMapping("/add")
    public CartItem addCartItem(@RequestBody CartItemRequest request) {
        return cartService.addCartItem(request);
    }
    @DeleteMapping("/remove/{cartItemId}")
    public void removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
    }
    @GetMapping("/total/{userId}")
    public double calculateTotalPrice(@PathVariable Long userId) {
        return cartService.calculateTotalPrice(userId);
    }
    @DeleteMapping("/clear/{userId}")
    public void clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
    }
}

