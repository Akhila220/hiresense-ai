package com.hiresense.dto;

import com.hiresense.entity.Job;
import java.util.List;

public class JobMatchDto {
    private Job job;
    private int matchPercentage;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> recommendations;
    private boolean saved;

    public JobMatchDto() {
    }

    public JobMatchDto(Job job, int matchPercentage, List<String> matchedSkills, List<String> missingSkills, List<String> recommendations, boolean saved) {
        this.job = job;
        this.matchPercentage = matchPercentage;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.recommendations = recommendations;
        this.saved = saved;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
