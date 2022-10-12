package org.project.onlineelectronicmarket.service;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.repository.GoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class GoodService {

        private final GoodRepository goodRepository;

        @Autowired
        public GoodService(GoodRepository goodRepository) {
                this.goodRepository = goodRepository;
        }

        public Good save(Good good) {
                return goodRepository.save(good);
        }

        public void delete(Good good) {
                goodRepository.delete(good);
        }

        @Modifying
        public Optional<Good> update(Good newGood) {
                Optional<Good> oldGood = findById(newGood.getId());

                if (oldGood.isPresent()) {
                        Good savedGood = save(newGood);
                        return Optional.of(savedGood);
                } else {
                        return oldGood;
                }
        }

        public Optional<Good> findById(Long id) {
                return goodRepository.findById(id);
        }

        public List<Good> findAll() {
                return goodRepository.findAll();
        }

        public List<Good> findAllByOrderByName() {
                return goodRepository.findAllByOrderByName();
        }

        public List<Good> findByNameContaining(String part) {
                return goodRepository.findByNameContainingIgnoreCase(part);
        }

        public List<Good> findByCompanyContaining(String part) {
                return goodRepository.findByCompanyContainingIgnoreCase(part);
        }

        public List<Good> findByAssemblyPlaceContaining(String part) {
                return goodRepository.findByAssemblyPlaceContainingIgnoreCase(part);
        }

        public List<Good> findByDescriptionContaining(String part) {
                return goodRepository.findByDescriptionContainingIgnoreCase(part);
        }

        public boolean hasOrderEntries(Good param) {
                return goodRepository.hasOrderEntries(param);
        }
}
