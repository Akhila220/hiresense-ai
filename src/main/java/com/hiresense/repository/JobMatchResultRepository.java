package com.hiresense.repository;

import com.hiresense.entity.Job;
import com.hiresense.entity.JobMatchResult;
import com.hiresense.entity.ResumeAnalysis;
import com.hiresense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobMatchResultRepository extends JpaRepository<JobMatchResult, Long> {
    List<JobMatchResult> findByResumeAnalysisOrderByMatchPercentageDesc(ResumeAnalysis analysis);
    Optional<JobMatchResult> findByResumeAnalysisAndJob(ResumeAnalysis analysis, Job job);
    List<JobMatchResult> findByResumeAnalysisResumeUserOrderByCreatedAtDesc(User user);
    long countByResumeAnalysisResumeUser(User user);
}
