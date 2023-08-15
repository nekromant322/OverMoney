package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugReportRepository extends JpaRepository<BugReport, Long> {

    @Query(value = "SELECT * FROM bug_reports ORDER BY id DESC", nativeQuery = true)
    List<BugReport> findAllOrderedByIdDesc();
}