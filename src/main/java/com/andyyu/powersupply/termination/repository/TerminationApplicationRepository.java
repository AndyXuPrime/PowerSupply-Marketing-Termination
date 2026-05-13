package com.andyyu.powersupply.termination.repository;

import com.andyyu.powersupply.termination.domain.TerminationApplication;
import com.andyyu.powersupply.termination.domain.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerminationApplicationRepository extends JpaRepository<TerminationApplication, Long> {

    List<TerminationApplication> findByStatusOrderByCreatedAtDesc(WorkflowStatus status);

    List<TerminationApplication> findByCustomerNameContainingIgnoreCaseOrApplicationNoContainingIgnoreCaseOrderByCreatedAtDesc(
            String customerName, String applicationNo);
}
