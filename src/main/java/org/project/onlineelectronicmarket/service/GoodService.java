package org.project.onlineelectronicmarket.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.repository.GoodRepository;
import org.project.onlineelectronicmarket.util.pagination.Paged;
import org.project.onlineelectronicmarket.util.pagination.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

        public Set<Good> findAllMatches(String query) {
                return Stream.of( this.findByNameContaining(query), this.findByCompanyContaining(query),
                                this.findByAssemblyPlaceContaining(query), this.findByDescriptionContaining(query) )
                        .flatMap(Collection::stream).collect(Collectors.toSet());
        }

        public Paged<Good> getPage(int pageNumber, int size) {
                PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.Direction.ASC, "id");
                Page<Good> goodPage = goodRepository.findAll(request);
                return new Paged<>(goodPage, Paging.of(goodPage.getTotalPages(), pageNumber, size));
        }
}
