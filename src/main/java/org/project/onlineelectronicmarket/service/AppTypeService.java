package org.project.onlineelectronicmarket.service;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.AppType;
import org.project.onlineelectronicmarket.repository.AppTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class AppTypeService {

    private final AppTypeRepository appTypeRepository;

    @Autowired
    public AppTypeService(AppTypeRepository appTypeRepository){
        this.appTypeRepository = appTypeRepository;
    }

    public AppType save(AppType appType) {
        return appTypeRepository.save(appType);
    }

    public void delete(AppType appType) {
        appTypeRepository.delete(appType);
    }

    @Modifying
    public Optional<AppType> update(AppType newAppType) {
        Optional<AppType> oldAppType = findById(newAppType.getId());

        if (oldAppType.isPresent()) {
            AppType savedAppType = save(newAppType);
            return Optional.of(savedAppType);
        } else {
            return oldAppType;
        }
    }

    public Optional<AppType> findById(Long id) {
        return appTypeRepository.findById(id);
    }

    public Optional<AppType> findByName(String name) {
        return appTypeRepository.findByName(name);
    }

    public List<AppType> findAllByOrderByName() {
        return appTypeRepository.findAllByOrderByName();
    }
}
