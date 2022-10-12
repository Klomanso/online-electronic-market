package org.project.onlineelectronicmarket.service;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  	private final OrderRepository orderRepository;

	@Autowired
	public OrderService(OrderRepository orderRepository){
		this.orderRepository = orderRepository;
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

	public Optional<Order> findById(Long id) {
		return orderRepository.findById(id);
	}

	public List<Order> findAllByOrderByOrderedAtDesc() {
		return orderRepository.findAllByOrderByOrderedAtDesc();
	}
}
