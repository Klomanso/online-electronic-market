package org.project.onlineelectronicmarket.repository;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.AppType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AppTypeRepository extends JpaRepository<AppType, Long> {
        Optional<AppType> findByName(String name);

        List<AppType> findAllByOrderByName();
}
