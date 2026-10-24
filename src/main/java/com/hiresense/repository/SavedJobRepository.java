package com.hiresense.repository;

import com.hiresense.entity.Job;
import com.hiresense.entity.SavedJob;
import com.hiresense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByUserOrderBySavedAtDesc(User user);
    boolean existsByUserAndJob(User user, Job job);
    Optional<SavedJob> findByUserAndJob(User user, Job job);
    long countByUser(User user);
    void deleteByUserAndJob(User user, Job job);
}
