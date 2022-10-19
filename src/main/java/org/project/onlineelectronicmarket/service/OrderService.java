package org.project.onlineelectronicmarket.service;

import org.project.onlineelectronicmarket.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

        Order save(Order order);

        void delete(Order order);

        Optional<Order> update(Order newOrder);

        boolean setOrderStatus(Order order);

        Optional<Order> findById(Long id);

        List<Order> findAllByOrderByOrderedAtDesc();
}
