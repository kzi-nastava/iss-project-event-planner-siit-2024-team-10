package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company,Integer> {

}
