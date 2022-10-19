package org.project.onlineelectronicmarket.service;

import org.project.onlineelectronicmarket.model.Good;

import java.util.List;
import java.util.Optional;

public interface GoodService {

        Good save(Good good);

        void delete(Good good);

        Optional<Good> update(Good newGood);

        Optional<Good> findById(Long id);

        List<Good> findAll();

        List<Good> findAllByOrderByName();

        List<Good> findByNameContaining(String part);

        List<Good> findByCompanyContaining(String part);

        List<Good> findByAssemblyPlaceContaining(String part);

        List<Good> findByDescriptionContaining(String part);

        boolean hasOrderEntries(Good param);

}
