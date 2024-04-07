package com.toyota.selling.repository;

import com.toyota.selling.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, String> {
    Sale findByBillId(String billId);
}
