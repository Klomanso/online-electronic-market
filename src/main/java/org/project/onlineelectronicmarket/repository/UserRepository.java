package org.project.onlineelectronicmarket.repository;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.Status;
import org.project.onlineelectronicmarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public interface UserRepository extends JpaRepository<User, Long> {
        List<User> findAllByOrderByName();

        boolean existsByName(String name);

        User findByName(String name);

        @Query("select u.orders from User u where u.id = :id")
        List<Order> findUserOrdersById(@Param("id") Long id);
}
