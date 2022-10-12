package org.project.onlineelectronicmarket.repository;

import java.util.List;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User, Long> {
        List<User> findAllByOrderByName();

        @Query("select u.orders from User u where u.id = :id")
        List<Order> findUserOrdersById(@Param("id") Long id);
}
