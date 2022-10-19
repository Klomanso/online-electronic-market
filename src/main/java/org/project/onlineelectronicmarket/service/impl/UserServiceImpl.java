package org.project.onlineelectronicmarket.service.impl;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.repository.UserRepository;
import org.project.onlineelectronicmarket.service.UserService;
import org.project.onlineelectronicmarket.util.pagination.Paged;
import org.project.onlineelectronicmarket.util.pagination.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;

        @Autowired
        public UserServiceImpl(UserRepository userRepository) {
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

        public Paged<User> getPage(int pageNumber, int size) {
                PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.Direction.ASC, "id");
                Page<User> usersPage = userRepository.findAll(request);
                return new Paged<>(usersPage, Paging.of(usersPage.getTotalPages(), pageNumber, size));
        }
}
