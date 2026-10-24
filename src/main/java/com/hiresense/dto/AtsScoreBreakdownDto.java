package com.hiresense.dto;

import java.util.List;
import java.util.Map;

public class AtsScoreBreakdownDto {

    private int overallScore;
    private int sectionScore;     // Max 25
    private int skillsScore;      // Max 35
    private int impactScore;      // Max 25
    private int formattingScore;  // Max 15

    private String scoreGrade;    // Excellent, Good, Fair, Needs Work
    private String scoreSummary;

    private List<String> detectedSections;
    private List<String> missingSections;
    private List<String> extractedSkills;
    private Map<String, List<String>> skillsByCategory;

    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> suggestions;
    private boolean aiEnhanced;

    public AtsScoreBreakdownDto() {
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public int getSectionScore() {
        return sectionScore;
    }

    public void setSectionScore(int sectionScore) {
        this.sectionScore = sectionScore;
    }

    public int getSkillsScore() {
        return skillsScore;
    }

    public void setSkillsScore(int skillsScore) {
        this.skillsScore = skillsScore;
    }

    public int getImpactScore() {
        return impactScore;
    }

    public void setImpactScore(int impactScore) {
        this.impactScore = impactScore;
    }

    public int getFormattingScore() {
        return formattingScore;
    }

    public void setFormattingScore(int formattingScore) {
        this.formattingScore = formattingScore;
    }

    public String getScoreGrade() {
        return scoreGrade;
    }

    public void setScoreGrade(String scoreGrade) {
        this.scoreGrade = scoreGrade;
    }

    public String getScoreSummary() {
        return scoreSummary;
    }

    public void setScoreSummary(String scoreSummary) {
        this.scoreSummary = scoreSummary;
    }

    public List<String> getDetectedSections() {
        return detectedSections;
    }

    public void setDetectedSections(List<String> detectedSections) {
        this.detectedSections = detectedSections;
    }

    public List<String> getMissingSections() {
        return missingSections;
    }

    public void setMissingSections(List<String> missingSections) {
        this.missingSections = missingSections;
    }

    public List<String> getExtractedSkills() {
        return extractedSkills;
    }

    public void setExtractedSkills(List<String> extractedSkills) {
        this.extractedSkills = extractedSkills;
    }

    public Map<String, List<String>> getSkillsByCategory() {
        return skillsByCategory;
    }

    public void setSkillsByCategory(Map<String, List<String>> skillsByCategory) {
        this.skillsByCategory = skillsByCategory;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public boolean isAiEnhanced() {
        return aiEnhanced;
    }

    public void setAiEnhanced(boolean aiEnhanced) {
        this.aiEnhanced = aiEnhanced;
    }
}
