package org.project.onlineelectronicmarket.service;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.OrderGood;
import org.project.onlineelectronicmarket.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

        private final OrderRepository orderRepository;

        private final StatusService statusService;

        @Autowired
        public OrderService(OrderRepository orderRepository, StatusService statusService) {
                this.orderRepository = orderRepository;
                this.statusService = statusService;
        }

        public Order save(Order order) {
                return orderRepository.save(order);
        }

        public void delete(Order order) {
                orderRepository.delete(order);
        }

        @Modifying
        public Optional<Order> update(Order newOrder) {
                Optional<Order> oldOrder = findById(newOrder.getId());

                if (oldOrder.isPresent()) {
                        Order savedOrder = save(newOrder);
                        return Optional.of(savedOrder);
                } else {
                        return oldOrder;
                }
        }

        public boolean setOrderStatus(Order order) {
                if (order.getStatus().equals(statusService.processing())) {
                        order.setStatus(statusService.complete());
                        return true;
                } else if (order.getStatus().equals(statusService.complete())) {
                        order.setStatus(statusService.delivered());
                        return true;
                } else {
                        return false;
                }
        }

        public double getTotalPrice(Order order) {
                double totalPrice = 0.0;
                List<OrderGood> items = order.getGoodItems();
                for (OrderGood item : items) {
                        totalPrice += item.getQuantity() * item.getGood().getPrice();
                }
                return totalPrice;
        }

        public Optional<Order> findById(Long id) {
                return orderRepository.findById(id);
        }

        public List<Order> findAllByOrderByOrderedAtDesc() {
                return orderRepository.findAllByOrderByOrderedAtDesc();
        }
}
