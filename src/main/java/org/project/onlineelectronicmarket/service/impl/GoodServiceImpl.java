package org.project.onlineelectronicmarket.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.project.onlineelectronicmarket.model.AppType;
import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.repository.GoodRepository;
import org.project.onlineelectronicmarket.repository.impl.GoodFilterRepositoryImpl;
import org.project.onlineelectronicmarket.service.GoodService;
import org.project.onlineelectronicmarket.util.filter.Filter;
import org.project.onlineelectronicmarket.util.filter.QueryOperator;
import org.project.onlineelectronicmarket.util.pagination.Paged;
import org.project.onlineelectronicmarket.util.pagination.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class GoodServiceImpl implements GoodService {

        private final GoodRepository goodRepository;
        private final GoodFilterRepositoryImpl goodFilterRepository;


        @Autowired
        public GoodServiceImpl(GoodRepository goodRepository, GoodFilterRepositoryImpl goodFilterRepository) {
                this.goodRepository = goodRepository;
                this.goodFilterRepository = goodFilterRepository;
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

        public List<Good> findAllByPriceBetween(Double from, Double to) {
                return goodRepository.findAllByPriceBetween(from, to);
        }

        public List<Good> findAllByQuantityBetween(Integer from, Integer to) {
                return goodRepository.findAllByQuantityBetween(from, to);
        }

        public List<Good> findAllByAppTypeName(String appType_name) {
                return goodRepository.findAllByAppTypeName(appType_name);
        }

        public Set<Good> findAllMatches(String query) {
                return Stream.of(this.findByNameContaining(query), this.findByCompanyContaining(query),
                                this.findByAssemblyPlaceContaining(query), this.findByDescriptionContaining(query))
                        .flatMap(Collection::stream).collect(Collectors.toSet());
        }

        public Paged<Good> getPage(int pageNumber, int size) {
                PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.Direction.ASC, "id");
                Page<Good> goodPage = goodRepository.findAll(request);
                return new Paged<>(goodPage, Paging.of(goodPage.getTotalPages(), pageNumber, size));
        }

        public List<Good> findAllByFilter(Map<String, String[]> params) {
                return goodFilterRepository.getQueryResult(getFilters(params));
        }

        private List<Filter> getFilters(Map<String, String[]> params) {
                Filter name = Filter.builder()
                        .field("name")
                        .value(Stream.of(params.get("goodName")).findFirst().orElse(""))
                        .operator(QueryOperator.LIKE)
                        .build();
                Filter company = Filter.builder()
                        .field("company")
                        .value(Stream.of(params.get("goodCompany")).findFirst().orElse(""))
                        .operator(QueryOperator.LIKE)
                        .build();
                Filter assemblyPlace = Filter.builder()
                        .field("assemblyPlace")
                        .value(Stream.of(params.get("aPlace")).findFirst().orElse(""))
                        .operator(QueryOperator.LIKE)
                        .build();
                Filter description = Filter.builder()
                        .field("description")
                        .value(Stream.of(params.get("description")).findFirst().orElse(""))
                        .operator(QueryOperator.LIKE)
                        .build();
                Filter minPrice = Filter.builder()
                        .field("price")
                        .value(Stream.of(params.get("minPrice")).findFirst().orElse(""))
                        .operator(QueryOperator.GREATER_THAN)
                        .build();
                Filter maxPrice = Filter.builder()
                        .field("price")
                        .value(Stream.of((params.get("maxPrice"))).findFirst().orElse(""))
                        .operator(QueryOperator.LESS_THAN)
                        .build();
                Filter minQuantity = Filter.builder()
                        .field("quantity")
                        .value(Stream.of((params.get("minQuantity"))).findFirst().orElse(""))
                        .operator(QueryOperator.GREATER_THAN)
                        .build();
                Filter maxQuantity = Filter.builder()
                        .field("quantity")
                        .value(Stream.of((params.get("maxQuantity"))).findFirst().orElse(""))
                        .operator(QueryOperator.LESS_THAN)
                        .build();

                Filter appType;
                if (params.containsKey("goodType")) {
                        appType = Filter.builder()
                                .field("appType")
                                .values(Stream.of(params.get("goodType")).collect(Collectors.toList()))
                                .operator(QueryOperator.IN)
                                .build();
                } else {
                        appType = Filter.builder()
                                .field("appType")
                                .values(new ArrayList<>())
                                .operator(QueryOperator.IN)
                                .build();
                }

                List<Filter> filters = new ArrayList<>();
                Collections.addAll(filters, name, company, assemblyPlace, description, appType,
                        minPrice, maxPrice, minQuantity, maxQuantity);

               return filters;
        }
}
