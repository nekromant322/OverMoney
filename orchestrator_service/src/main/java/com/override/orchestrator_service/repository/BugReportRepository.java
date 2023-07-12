package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugReportRepository extends JpaRepository<BugReport, Long> {

}
