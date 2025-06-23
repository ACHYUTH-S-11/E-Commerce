package com.platform.controller;
import org.springframework.web.bind.annotation.*;
import com.platform.entity.Order;
import com.platform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.model.OrderRequest;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    @PostMapping("/create")
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @PutMapping("/status/{orderId}")
    public Order updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @PutMapping("/payment/{orderId}")
    public Order updatePaymentStatus(@PathVariable Long orderId, @RequestParam String status) {
        return orderService.updatePaymentStatus(orderId, status);
    }

    

}
