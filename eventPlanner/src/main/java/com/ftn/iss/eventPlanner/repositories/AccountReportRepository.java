package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.AccountReport;
import com.ftn.iss.eventPlanner.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountReportRepository extends JpaRepository<AccountReport,Integer> {
    boolean existsByReporter_IdAndReportee_Id(int reporterId, int reporteeId);
    Optional<AccountReport> findTopByReportee_IdAndStatusOrderByProcessingTimestampDesc(int reporteeId, Status status);
}
