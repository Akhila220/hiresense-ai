package com.hiresense.repository;

import com.hiresense.entity.ResumeAnalysis;
import com.hiresense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    Optional<ResumeAnalysis> findTopByResumeUserOrderByCreatedAtDesc(User user);
    Optional<ResumeAnalysis> findByIdAndResumeUser(Long id, User user);
    List<ResumeAnalysis> findByResumeUserOrderByCreatedAtDesc(User user);
    Optional<ResumeAnalysis> findTopByResumeIdOrderByCreatedAtDesc(Long resumeId);
}
