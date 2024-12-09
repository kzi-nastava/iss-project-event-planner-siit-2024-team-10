package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
}
