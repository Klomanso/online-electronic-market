package org.project.onlineelectronicmarket.service;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

        User save(User user);

        void delete(User user);

        Optional<User> update(User newUser);

        Optional<User> findById(Long id);

        List<User> findAll();

        List<User> findAllByOrderByName();

        List<Order> findUserOrdersById(Long id);

}
