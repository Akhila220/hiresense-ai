package com.hiresense.dto;

public class JobFilterDto {
    private String keyword;
    private String location;
    private String employmentType;
    private String experienceLevel;

    public JobFilterDto() {
    }

    public JobFilterDto(String keyword, String location, String employmentType, String experienceLevel) {
        this.keyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        this.location = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        this.employmentType = (employmentType != null && !employmentType.trim().isEmpty()) ? employmentType.trim() : null;
        this.experienceLevel = (experienceLevel != null && !experienceLevel.trim().isEmpty()) ? experienceLevel.trim() : null;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = (employmentType != null && !employmentType.trim().isEmpty()) ? employmentType.trim() : null;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = (experienceLevel != null && !experienceLevel.trim().isEmpty()) ? experienceLevel.trim() : null;
    }
}
