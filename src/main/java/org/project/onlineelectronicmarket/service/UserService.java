package org.project.onlineelectronicmarket.service;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class UserService {

        private final UserRepository userRepository;

        @Autowired
        public UserService(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        public User save(User user) {
                return userRepository.save(user);
        }

        public void delete(User user) {
                userRepository.delete(user);
        }

        @Modifying
        public Optional<User> update(User newUser) {
                Optional<User> oldUser = findById(newUser.getId());

                if (oldUser.isPresent()) {
                        User savedUser = save(newUser);
                        return Optional.of(savedUser);
                } else {
                        return oldUser;
                }
        }

        public Optional<User> findById(Long id) {
                return userRepository.findById(id);
        }

        public List<User> findAll() {
                return userRepository.findAll();
        }

        public List<User> findAllByOrderByName() {
                return userRepository.findAllByOrderByName();
        }

        public List<Order> findUserOrdersById(Long id) {
                return userRepository.findUserOrdersById(id);
        }
}
