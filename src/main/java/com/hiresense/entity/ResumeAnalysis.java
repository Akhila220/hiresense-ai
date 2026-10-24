package com.hiresense.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resume_analyses")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "overall_ats_score", nullable = false)
    private Integer overallAtsScore;

    @Column(name = "section_score", nullable = false)
    private Integer sectionScore;

    @Column(name = "skills_score", nullable = false)
    private Integer skillsScore;

    @Column(name = "impact_score", nullable = false)
    private Integer impactScore;

    @Column(name = "formatting_score", nullable = false)
    private Integer formattingScore;

    @Column(name = "detected_sections", columnDefinition = "TEXT")
    private String detectedSections;

    @Column(name = "extracted_skills", columnDefinition = "TEXT")
    private String extractedSkills;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions;

    @Column(name = "is_ai_enhanced", nullable = false)
    private Boolean isAiEnhanced = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "resumeAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobMatchResult> matchResults = new ArrayList<>();

    public ResumeAnalysis() {
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public Integer getOverallAtsScore() {
        return overallAtsScore;
    }

    public void setOverallAtsScore(Integer overallAtsScore) {
        this.overallAtsScore = overallAtsScore;
    }

    public Integer getSectionScore() {
        return sectionScore;
    }

    public void setSectionScore(Integer sectionScore) {
        this.sectionScore = sectionScore;
    }

    public Integer getSkillsScore() {
        return skillsScore;
    }

    public void setSkillsScore(Integer skillsScore) {
        this.skillsScore = skillsScore;
    }

    public Integer getImpactScore() {
        return impactScore;
    }

    public void setImpactScore(Integer impactScore) {
        this.impactScore = impactScore;
    }

    public Integer getFormattingScore() {
        return formattingScore;
    }

    public void setFormattingScore(Integer formattingScore) {
        this.formattingScore = formattingScore;
    }

    public String getDetectedSections() {
        return detectedSections;
    }

    public void setDetectedSections(String detectedSections) {
        this.detectedSections = detectedSections;
    }

    public String getExtractedSkills() {
        return extractedSkills;
    }

    public void setExtractedSkills(String extractedSkills) {
        this.extractedSkills = extractedSkills;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public Boolean getIsAiEnhanced() {
        return isAiEnhanced;
    }

    public void setIsAiEnhanced(Boolean isAiEnhanced) {
        this.isAiEnhanced = isAiEnhanced;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<JobMatchResult> getMatchResults() {
        return matchResults;
    }

    public void setMatchResults(List<JobMatchResult> matchResults) {
        this.matchResults = matchResults;
    }
}
