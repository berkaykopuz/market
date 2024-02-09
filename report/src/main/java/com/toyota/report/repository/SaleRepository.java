package com.toyota.report.repository;

import com.toyota.report.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, String> {
    @Query("SELECT s FROM Sale s " +
            "WHERE YEAR(s.saleDate) = :year AND MONTH(s.saleDate) = :month AND DAY(s.saleDate) = :day")
    List<Sale> findBySaleDate(int year, int month, int day);

}
