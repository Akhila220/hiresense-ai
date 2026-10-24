package com.hiresense.dto;

import com.hiresense.entity.JobMatchResult;
import com.hiresense.entity.Resume;
import com.hiresense.entity.ResumeAnalysis;
import com.hiresense.entity.SavedJob;

import java.util.List;
import java.util.Map;

public class DashboardSummaryDto {
    private long totalResumes;
    private Integer latestAtsScore;
    private String latestScoreGrade;
    private long totalSavedJobs;
    private long totalMatches;

    private Resume latestResume;
    private ResumeAnalysis latestAnalysis;
    private AtsScoreBreakdownDto latestBreakdown;

    private List<Resume> recentResumes;
    private List<SavedJob> savedJobs;
    private List<JobMatchResult> recentMatches;
    private Map<String, Integer> skillCategoryCounts;

    public DashboardSummaryDto() {
    }

    public long getTotalResumes() {
        return totalResumes;
    }

    public void setTotalResumes(long totalResumes) {
        this.totalResumes = totalResumes;
    }

    public Integer getLatestAtsScore() {
        return latestAtsScore;
    }

    public void setLatestAtsScore(Integer latestAtsScore) {
        this.latestAtsScore = latestAtsScore;
    }

    public String getLatestScoreGrade() {
        return latestScoreGrade;
    }

    public void setLatestScoreGrade(String latestScoreGrade) {
        this.latestScoreGrade = latestScoreGrade;
    }

    public long getTotalSavedJobs() {
        return totalSavedJobs;
    }

    public void setTotalSavedJobs(long totalSavedJobs) {
        this.totalSavedJobs = totalSavedJobs;
    }

    public long getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(long totalMatches) {
        this.totalMatches = totalMatches;
    }

    public Resume getLatestResume() {
        return latestResume;
    }

    public void setLatestResume(Resume latestResume) {
        this.latestResume = latestResume;
    }

    public ResumeAnalysis getLatestAnalysis() {
        return latestAnalysis;
    }

    public void setLatestAnalysis(ResumeAnalysis latestAnalysis) {
        this.latestAnalysis = latestAnalysis;
    }

    public AtsScoreBreakdownDto getLatestBreakdown() {
        return latestBreakdown;
    }

    public void setLatestBreakdown(AtsScoreBreakdownDto latestBreakdown) {
        this.latestBreakdown = latestBreakdown;
    }

    public List<Resume> getRecentResumes() {
        return recentResumes;
    }

    public void setRecentResumes(List<Resume> recentResumes) {
        this.recentResumes = recentResumes;
    }

    public List<SavedJob> getSavedJobs() {
        return savedJobs;
    }

    public void setSavedJobs(List<SavedJob> savedJobs) {
        this.savedJobs = savedJobs;
    }

    public List<JobMatchResult> getRecentMatches() {
        return recentMatches;
    }

    public void setRecentMatches(List<JobMatchResult> recentMatches) {
        this.recentMatches = recentMatches;
    }

    public Map<String, Integer> getSkillCategoryCounts() {
        return skillCategoryCounts;
    }

    public void setSkillCategoryCounts(Map<String, Integer> skillCategoryCounts) {
        this.skillCategoryCounts = skillCategoryCounts;
    }
}
