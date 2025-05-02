package com.falcon.falcon.repository;

import com.falcon.falcon.entity.FlagSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlagSubmissionRepository extends JpaRepository<FlagSubmission, Long> {
}
