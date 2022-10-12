package org.project.onlineelectronicmarket.repository;

import java.util.List;

import org.project.onlineelectronicmarket.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
        List<Order> findAllByOrderByOrderedAtDesc();
}
