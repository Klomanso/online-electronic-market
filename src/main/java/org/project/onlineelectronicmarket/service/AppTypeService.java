package org.project.onlineelectronicmarket.service;

import org.project.onlineelectronicmarket.model.AppType;

import java.util.List;
import java.util.Optional;

public interface AppTypeService {
        AppType save(AppType appType);

        void delete(AppType appType);

        Optional<AppType> update(AppType newAppType);

        Optional<AppType> findById(Long id);

        Optional<AppType> findByName(String name);

        List<AppType> findAllByOrderByName();

}