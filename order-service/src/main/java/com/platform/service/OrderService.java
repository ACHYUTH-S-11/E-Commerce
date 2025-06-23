package com.platform.service;

import org.springframework.stereotype.Service;
import com.platform.repository.OrderRepository;
import com.platform.feign.CartClient;
import com.platform.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.model.OrderRequest;
import com.platform.model.ProductDTO;
import com.platform.feign.ProductClient;
import com.platform.model.CartItemDTO;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private ProductClient productClient;

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order createOrder(OrderRequest request) {
        double totalPrice = cartClient.getCartTotal(request.getUserId());
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setShippingAddress(request.getShippingAddress());
        order.setTotalPrice(totalPrice);
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    public Order updatePaymentStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setPaymentStatus(status);
        return orderRepository.save(order);
    }

    public Order placeOrder(OrderRequest request) {
        List<CartItemDTO> cartItems = cartClient.getCartItemsByUserId(request.getUserId());
        System.out.println("Cart items: " + cartItems);
        double totalPrice = 0.0;

        for (CartItemDTO item : cartItems) {
            ProductDTO product = productClient.getProductById(item.getProductId());
            System.out.println("Fetched product: " + product);
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            totalPrice += product.getPrice() * item.getQuantity();
        }

        for (CartItemDTO item : cartItems) {
            productClient.updateProduct(item.getProductId(), item.getQuantity());
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setShippingAddress(request.getShippingAddress());
        order.setTotalPrice(totalPrice);
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        Order savedOrder = orderRepository.save(order);
        cartClient.clearCart(request.getUserId());

        return savedOrder;
    }
}
