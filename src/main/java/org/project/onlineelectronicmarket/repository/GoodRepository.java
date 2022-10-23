package org.project.onlineelectronicmarket.repository;

import java.util.List;

import org.project.onlineelectronicmarket.model.AppType;
import org.project.onlineelectronicmarket.model.Good;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public interface GoodRepository extends JpaRepository<Good, Long> {
        List<Good> findAllByOrderByName();

        List<Good> findByNameContainingIgnoreCase(String part);

        List<Good> findByCompanyContainingIgnoreCase(String part);

        List<Good> findByAssemblyPlaceContainingIgnoreCase(String part);

        List<Good> findByDescriptionContainingIgnoreCase(String part);

        @Query("select count(*) > 0 from OrderGood entry where entry.good = :param")
        Boolean hasOrderEntries(@Param("param") Good param);

        List<Good> findAllByPriceBetween(Double from, Double to);

        List<Good> findAllByQuantityBetween(Integer from, Integer to);

        List<Good> findAllByAppTypeName(String appType_name);
}
