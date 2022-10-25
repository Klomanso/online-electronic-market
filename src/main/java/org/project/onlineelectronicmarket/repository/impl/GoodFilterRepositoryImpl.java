package org.project.onlineelectronicmarket.repository.impl;

import org.project.onlineelectronicmarket.model.AppType;
import org.project.onlineelectronicmarket.model.AppType_;
import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.model.Good_;
import org.project.onlineelectronicmarket.repository.AppTypeRepository;
import org.project.onlineelectronicmarket.repository.GoodRepository;
import org.project.onlineelectronicmarket.util.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.jpa.domain.Specification.where;

@Repository
public class GoodFilterRepositoryImpl {

        private final GoodRepository goodRepository;
        private final AppTypeRepository appTypeRepository;

        @Autowired
        public GoodFilterRepositoryImpl(GoodRepository goodRepository, AppTypeRepository appTypeRepository) {
                this.goodRepository = goodRepository;
                this.appTypeRepository = appTypeRepository;
        }

        public List<Good> getQueryResult(List<Filter> filters) {
                if (filters.size() > 0) {
                        return goodRepository.findAll(getSpecificationFromFilters(filters));
                } else {
                        return goodRepository.findAll();
                }
        }

        private Specification<Good> getSpecificationFromFilters(List<Filter> filter) {
                Specification<Good> specification = where(createSpecification(filter.remove(0)));
                for (Filter input : filter) {
                        specification = specification.and(createSpecification(input));
                }
                return specification;
        }

        private Specification<Good> createSpecification(Filter input) {
                return switch (input.getOperator()) {
                        case EQUALS -> (root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get(input.getField()),
                                        castToRequiredType(root.get(input.getField()).getJavaType(),
                                                input.getValue()));

                        case NOT_EQ -> (root, query, criteriaBuilder) ->
                                criteriaBuilder.notEqual(root.get(input.getField()),
                                        castToRequiredType(root.get(input.getField()).getJavaType(),
                                                input.getValue()));

                        case GREATER_THAN -> (root, query, criteriaBuilder) ->
                                criteriaBuilder.gt(root.get(input.getField()),
                                        (Number) castToRequiredType(root.get(input.getField()).getJavaType(),
                                                input.getValue()));

                        case LESS_THAN -> (root, query, criteriaBuilder) ->
                                criteriaBuilder.lt(root.get(input.getField()),
                                        (Number) castToRequiredType(root.get(input.getField()).getJavaType(),
                                                input.getValue()));

                        case LIKE -> (root, query, criteriaBuilder) ->
                                criteriaBuilder.like(root.get(input.getField()), "%" + input.getValue() + "%");


                        case IN -> (root, query, criteriaBuilder) -> {
                                Join<Good, AppType> goodAppTypeJoin = root.join(Good_.APP_TYPE);

                                if (input.getValues().isEmpty()) {
                                        List<String> types = appTypeRepository.findAll().stream().map(AppType::getName)
                                                .toList();
                                        return goodAppTypeJoin.get(AppType_.name).in(types);
                                }

                                return goodAppTypeJoin.get(AppType_.name).in(input.getValues());
                        };
                };
        }

        private Object castToRequiredType(Class fieldType, String value) {
                if (fieldType.isAssignableFrom(Double.class)) {
                        return Double.valueOf(value);
                } else if (fieldType.isAssignableFrom(Integer.class)) {
                        return Integer.valueOf(value);
                } else if (Enum.class.isAssignableFrom(fieldType)) {
                        return Enum.valueOf(fieldType, value);
                }
                return null;
        }

        private Object castToRequiredType(Class fieldType, List<String> value) {
                List<Object> lists = new ArrayList<>();
                for (String s : value) {
                        lists.add(castToRequiredType(fieldType, s));
                }
                return lists;
        }

        private Specification<Good> nameLike(String name) {
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Good_.NAME), "%" + name + "%");
        }


        private Specification<Good> pricesAreBetween(Double min, Double max) {
                return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Good_.PRICE), min, max);
        }

}
