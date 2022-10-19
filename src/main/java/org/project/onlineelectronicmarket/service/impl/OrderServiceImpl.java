package org.project.onlineelectronicmarket.service.impl;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.OrderGood;
import org.project.onlineelectronicmarket.repository.OrderRepository;
import org.project.onlineelectronicmarket.service.OrderService;
import org.project.onlineelectronicmarket.util.pagination.Paged;
import org.project.onlineelectronicmarket.util.pagination.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;

        private final StatusServiceImpl statusServiceImpl;

        @Autowired
        public OrderServiceImpl(OrderRepository orderRepository, StatusServiceImpl statusServiceImpl) {
                this.orderRepository = orderRepository;
                this.statusServiceImpl = statusServiceImpl;
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
                if (order.getStatus().equals(statusServiceImpl.processing())) {
                        order.setStatus(statusServiceImpl.complete());
                        return true;
                } else if (order.getStatus().equals(statusServiceImpl.complete())) {
                        order.setStatus(statusServiceImpl.delivered());
                        return true;
                } else {
                        return false;
                }
        }

        public Optional<Order> findById(Long id) {
                return orderRepository.findById(id);
        }

        public List<Order> findAllByOrderByOrderedAtDesc() {
                return orderRepository.findAllByOrderByOrderedAtDesc();
        }

        public double getTotalPrice(Order order) {
                double totalPrice = 0.0;
                List<OrderGood> items = order.getGoodItems();
                for (OrderGood item : items) {
                        totalPrice += item.getQuantity() * item.getGood().getPrice();
                }
                return totalPrice;
        }

        public Paged<Order> getPage(int pageNumber, int size) {
                PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.Direction.ASC, "id");
                Page<Order> orderPage = orderRepository.findAll(request);
                return new Paged<>(orderPage, Paging.of(orderPage.getTotalPages(), pageNumber, size));
        }
}
