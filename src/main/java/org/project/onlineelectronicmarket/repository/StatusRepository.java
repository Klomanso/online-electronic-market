package org.project.onlineelectronicmarket.repository;

import java.util.Optional;

import org.project.onlineelectronicmarket.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
        Optional<Status> findByName(String name);
}
