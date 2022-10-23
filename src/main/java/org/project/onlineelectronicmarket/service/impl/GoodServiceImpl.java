package org.project.onlineelectronicmarket.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.repository.GoodRepository;
import org.project.onlineelectronicmarket.service.GoodService;
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

        @Autowired
        public GoodServiceImpl(GoodRepository goodRepository) {
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

        public List<Good> findAllByPriceBetween(Double from, Double to) {
                return goodRepository.findAllByPriceBetween(from, to);
        }

        public List<Good> findAllByQuantityBetween(Integer from, Integer to) {
                return goodRepository.findAllByQuantityBetween(from, to);
        }

        public List<Good> findAllByAppTypeName(String appType_name) {
                return goodRepository.findAllByAppTypeName(appType_name);
        }

/*
        public Set<Good> findAllFilterMatches(Map<String, String[]> filterParams) {

                String goodNameParam = Stream.of(filterParams.get("goodName")).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("goodName param"));
                if ( ! goodNameParam.equals("")) {
                        List<Good> goodNameList = findByNameContaining(goodNameParam);
                }

                String goodCompany = Stream.of(filterParams.get("goodCompany")).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("goodCompany param"));
                if ( ! goodCompany.equals("")) {
                        List<Good> goodCompanyList = findByNameContaining(goodCompany);
                }

                String goodAssemblyPlace = Stream.of(filterParams.get("aPlace")).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("aPlace param"));
                if ( ! goodAssemblyPlace.equals("")) {
                        List<Good> goodAPlaceList = findByNameContaining(goodCompany);
                }

                String goodDescription = Stream.of(filterParams.get("description")).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("description param"));
                if ( ! goodDescription.equals("")) {
                        List<Good> goodDescriptionList = findByNameContaining(goodCompany);
                }

                boolean minPriceParamNotExist = Stream.of(filterParams.get("minPrice")).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("minPrice param"))
                        .equals("");
                double minPriceParam;
                if (minPriceParamNotExist) {
                        minPriceParam = 0.0;
                } else {
                        minPriceParam = Stream.of(filterParams.get("minPrice")).findFirst().map(Double::parseDouble)
                                .get();
                }

                boolean maxPriceParamNotExist = Stream.of(filterParams.get("maxPrice")).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("maxPrice param"))
                        .equals("");
                double maxPriceParam;
                if (maxPriceParamNotExist) {
                        maxPriceParam = 1_000_000.0;
                } else {
                        maxPriceParam = Stream.of(filterParams.get("maxPrice")).findFirst().map(Double::parseDouble)
                                .get();
                }

                if(Double.compare(minPriceParam, maxPriceParam) >= 0) {
                        minPriceParam = 0.0;
                }

                List<Good> priceRangeList = findAllByPriceBetween(minPriceParam, maxPriceParam);

                boolean minQuantityParamNotExist = Stream.of(filterParams.get("minQuantity")).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("minQuantity param"))
                        .equals("");
                int minQuantityParam;
                if (minQuantityParamNotExist) {
                        minQuantityParam = 0;
                } else {
                        minQuantityParam = Stream.of(filterParams.get("minQuantity")).findFirst().map(Integer::parseInt)
                                .get();
                }

                boolean maxQuantityParamNotExist = Stream.of(filterParams.get("maxQuantity")).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("maxQuantity param"))
                        .equals("");
                int maxQuantityParam;
                if (maxQuantityParamNotExist) {
                        maxQuantityParam = 100_000;
                } else {
                        maxQuantityParam = Stream.of(filterParams.get("maxQuantity")).findFirst().map(Integer::parseInt)
                                .get();
                }

                if(minQuantityParam >= maxQuantityParam) {
                        minQuantityParam = 0;
                }

                List<Good> quantityRangeList = findAllByQuantityBetween(minQuantityParam, maxQuantityParam);
*/
/*
                this.findAllByPriceBetween(
                        Arrays.stream(filterParams.get("minPrice")).map(Double::parseDouble)
                                .findFirst().orElseThrow(() -> new IllegalArgumentException("minPrice")),
                        Arrays.stream(filterParams.get("maxPrice")).map(Double::parseDouble)
                                .findFirst().orElseThrow(() -> new IllegalArgumentException("maxPrice"))
                );
*//*

                System.out.println();
                return null;
        }
*/

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
}
