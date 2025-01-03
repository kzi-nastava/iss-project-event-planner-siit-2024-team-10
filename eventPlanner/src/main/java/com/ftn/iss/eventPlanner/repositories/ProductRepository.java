package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product,Integer>, JpaSpecificationExecutor<Product> {
    @Query("SELECT MAX(p.currentDetails.price) FROM Product p")
    Double findMaxProductPrice();
}
