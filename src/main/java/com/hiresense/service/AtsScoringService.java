package com.hiresense.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiresense.dto.AtsScoreBreakdownDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AtsScoringService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Skill Taxonomy by Category
    private static final Map<String, List<String>> TAXONOMY = new LinkedHashMap<>();

    static {
        TAXONOMY.put("Languages", Arrays.asList(
            "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "C", "Go", "Rust", 
            "Ruby", "PHP", "Swift", "Kotlin", "SQL", "HTML", "HTML5", "CSS", "CSS3", "Bash", "Shell", "R"
        ));
        TAXONOMY.put("Frameworks & Web", Arrays.asList(
            "Spring Boot", "Spring Security", "Spring Data", "Spring MVC", "Hibernate", "JPA", 
            "React", "Angular", "Vue", "Node.js", "Express", "Django", "Flask", "FastAPI", 
            "Next.js", "Bootstrap", "Tailwind CSS", "Thymeleaf", "REST API", "GraphQL"
        ));
        TAXONOMY.put("Databases & Storage", Arrays.asList(
            "PostgreSQL", "MySQL", "MongoDB", "Redis", "Oracle", "SQLite", "Cassandra", 
            "Elasticsearch", "DynamoDB", "Firebase", "Snowflake"
        ));
        TAXONOMY.put("Cloud & DevOps", Arrays.asList(
            "AWS", "Azure", "Google Cloud", "GCP", "Docker", "Kubernetes", "CI/CD", 
            "Terraform", "Jenkins", "GitHub Actions", "Linux", "Nginx", "Microservices"
        ));
        TAXONOMY.put("Tools & Practices", Arrays.asList(
            "Git", "GitHub", "GitLab", "Maven", "Gradle", "JUnit", "Mockito", "Selenium", 
            "Postman", "Jira", "Agile", "Scrum", "Object-Oriented Programming", "OOP", 
            "Data Structures", "Algorithms"
        ));
        TAXONOMY.put("Soft Skills", Arrays.asList(
            "Problem Solving", "Leadership", "Communication", "Team Collaboration", 
            "Time Management", "Critical Thinking", "Mentorship", "Analytical Skills"
        ));
    }

    private static final List<String> ACTION_VERBS = Arrays.asList(
        "engineered", "architected", "developed", "implemented", "optimized", "designed",
        "accelerated", "scaled", "delivered", "automated", "reduced", "increased", "transformed",
        "resolved", "built", "spearheaded", "orchestrated", "streamlined", "created", "launched"
    );

    public AtsScoreBreakdownDto analyze(String rawText) {
        AtsScoreBreakdownDto dto = new AtsScoreBreakdownDto();
        String text = rawText != null ? rawText : "";
        String lowerText = text.toLowerCase();

        // 1. Section Detection
        List<String> detectedSections = new ArrayList<>();
        List<String> missingSections = new ArrayList<>();

        boolean hasContact = checkContactInfo(text);
        if (hasContact) detectedSections.add("Contact Information"); else missingSections.add("Contact Information");

        boolean hasSummary = containsPattern(lowerText, "\\b(summary|objective|profile|about me)\\b");
        if (hasSummary) detectedSections.add("Professional Summary"); else missingSections.add("Professional Summary");

        boolean hasExperience = containsPattern(lowerText, "\\b(experience|employment|work history|career history)\\b");
        if (hasExperience) detectedSections.add("Work Experience"); else missingSections.add("Work Experience");

        boolean hasEducation = containsPattern(lowerText, "\\b(education|academic|degree|university|college)\\b");
        if (hasEducation) detectedSections.add("Education"); else missingSections.add("Education");

        boolean hasSkills = containsPattern(lowerText, "\\b(skills|technical skills|technologies|competencies)\\b");
        if (hasSkills) detectedSections.add("Skills"); else missingSections.add("Skills");

        boolean hasProjects = containsPattern(lowerText, "\\b(projects|academic projects|personal projects|portfolio)\\b");
        if (hasProjects) detectedSections.add("Projects"); else missingSections.add("Projects");

        boolean hasCerts = containsPattern(lowerText, "\\b(certifications|certificates|licenses|achievements)\\b");
        if (hasCerts) detectedSections.add("Certifications / Achievements"); else missingSections.add("Certifications / Achievements");

        // Section Score (Max 25)
        int sectionScore = 0;
        if (hasContact) sectionScore += 5;
        if (hasExperience) sectionScore += 5;
        if (hasEducation) sectionScore += 5;
        if (hasSkills) sectionScore += 5;
        if (hasProjects || hasSummary) sectionScore += 5;
        dto.setSectionScore(sectionScore);

        // 2. Skills Extraction
        Map<String, List<String>> skillsByCategory = new LinkedHashMap<>();
        Set<String> allExtractedSkills = new LinkedHashSet<>();

        for (Map.Entry<String, List<String>> entry : TAXONOMY.entrySet()) {
            List<String> foundInCat = new ArrayList<>();
            for (String skill : entry.getValue()) {
                if (containsSkill(lowerText, skill.toLowerCase())) {
                    foundInCat.add(skill);
                    allExtractedSkills.add(skill);
                }
            }
            if (!foundInCat.isEmpty()) {
                skillsByCategory.put(entry.getKey(), foundInCat);
            }
        }

        // Skills Score (Max 35)
        int skillCount = allExtractedSkills.size();
        int skillsScore;
        if (skillCount >= 10) {
            skillsScore = 35;
        } else if (skillCount >= 7) {
            skillsScore = 28;
        } else if (skillCount >= 4) {
            skillsScore = 20;
        } else if (skillCount >= 1) {
            skillsScore = 12;
        } else {
            skillsScore = 5;
        }
        dto.setSkillsScore(skillsScore);

        // 3. Impact & Action Verbs (Max 25)
        int actionVerbCount = 0;
        for (String verb : ACTION_VERBS) {
            if (containsPattern(lowerText, "\\b" + Pattern.quote(verb) + "\\b")) {
                actionVerbCount++;
            }
        }

        // Quantifiable metrics: numbers followed by %, +, k, etc.
        boolean hasMetrics = Pattern.compile("(\\d+%|\\$\\d+|\\d+\\+?\\s*(users|clients|ms|seconds|teams|projects))").matcher(lowerText).find();
        int impactScore;
        if (actionVerbCount >= 5 && hasMetrics) {
            impactScore = 25;
        } else if (actionVerbCount >= 3) {
            impactScore = hasMetrics ? 20 : 16;
        } else if (actionVerbCount >= 1) {
            impactScore = 12;
        } else {
            impactScore = 6;
        }
        dto.setImpactScore(impactScore);

        // 4. Formatting & Length (Max 15)
        int wordCount = text.split("\\s+").length;
        int formattingScore = 0;
        if (wordCount >= 250 && wordCount <= 1200) {
            formattingScore += 10;
        } else if (wordCount > 1200) {
            formattingScore += 6; // too verbose
        } else {
            formattingScore += 4; // too short
        }

        boolean hasBullets = text.contains("•") || text.contains("- ") || text.contains("* ") || text.contains("\u2022");
        if (hasBullets) {
            formattingScore += 5;
        } else {
            formattingScore += 2;
        }
        dto.setFormattingScore(formattingScore);

        // Total Overall ATS Score (0 - 100)
        int totalScore = sectionScore + skillsScore + impactScore + formattingScore;
        totalScore = Math.max(10, Math.min(98, totalScore)); // realistically bounded
        dto.setOverallScore(totalScore);

        if (totalScore >= 80) {
            dto.setScoreGrade("Strong Match (A)");
            dto.setScoreSummary("Your resume demonstrates solid structure, prominent technical competencies, and good readability for automated tracking systems.");
        } else if (totalScore >= 65) {
            dto.setScoreGrade("Competitive (B)");
            dto.setScoreSummary("Your resume contains essential qualifications but can be elevated with deeper action verbs and missing industry keywords.");
        } else if (totalScore >= 50) {
            dto.setScoreGrade("Fair (C)");
            dto.setScoreSummary("Fundamental sections are present, but keyword density, impact metrics, and project demonstrations need improvement.");
        } else {
            dto.setScoreGrade("Needs Optimization (D)");
            dto.setScoreSummary("The resume is missing standard ATS section headings, technical keywords, or structured formatting.");
        }

        dto.setDetectedSections(detectedSections);
        dto.setMissingSections(missingSections);
        dto.setExtractedSkills(new ArrayList<>(allExtractedSkills));
        dto.setSkillsByCategory(skillsByCategory);

        // 5. Strengths
        List<String> strengths = new ArrayList<>();
        if (allExtractedSkills.size() >= 8) {
            strengths.add("Extensive technical skill coverage with " + allExtractedSkills.size() + " detected industry keywords.");
        }
        if (hasContact) {
            strengths.add("Clear contact headers found, facilitating recruiter communication.");
        }
        if (hasExperience && hasEducation) {
            strengths.add("Core chronological sections (Experience & Education) are explicitly defined.");
        }
        if (hasProjects) {
            strengths.add("Practical projects section included to showcase applied problem-solving.");
        }
        if (actionVerbCount >= 3) {
            strengths.add("Effective deployment of dynamic action verbs (" + actionVerbCount + " key action verbs identified).");
        }
        if (strengths.isEmpty()) {
            strengths.add("Document successfully parsed and text structure recognized.");
        }
        dto.setStrengths(strengths);

        // 6. Weaknesses & Suggestions
        List<String> weaknesses = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (!hasSummary) {
            weaknesses.add("Missing a concise Professional Summary or Objective statement.");
            suggestions.add("Add a 2-3 line Professional Summary at the top emphasizing your specialty and core stack.");
        }
        if (!hasProjects) {
            weaknesses.add("No distinct 'Projects' section identified.");
            suggestions.add("Add a dedicated Projects section with links to GitHub or live demos showcasing real-world problem solving.");
        }
        if (!hasMetrics) {
            weaknesses.add("Limited quantifiable business impact or performance metrics.");
            suggestions.add("Quantify your achievements using numbers (e.g., 'reduced API response times by 35%', 'handled 5,000+ daily transactions').");
        }
        if (allExtractedSkills.size() < 6) {
            weaknesses.add("Low density of explicit technical tool and framework keywords.");
            suggestions.add("Incorporate relevant database, cloud, and testing tools into a dedicated 'Technical Skills' table or list.");
        }
        if (!hasBullets) {
            weaknesses.add("Lack of distinct bullet points for experience accomplishments.");
            suggestions.add("Structure job responsibilities with bullet points rather than paragraphs to improve ATS parsing.");
        }
        if (!hasCerts) {
            suggestions.add("Consider adding relevant certifications or hackathon achievements to differentiate your profile.");
        }

        dto.setWeaknesses(weaknesses);
        dto.setSuggestions(suggestions);

        return dto;
    }

    private boolean checkContactInfo(String text) {
        // Look for email or phone
        boolean hasEmail = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}").matcher(text).find();
        boolean hasPhone = Pattern.compile("(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}").matcher(text).find();
        boolean hasLinkedIn = text.toLowerCase().contains("linkedin.com") || text.toLowerCase().contains("github.com");
        return hasEmail || (hasPhone && hasLinkedIn);
    }

    private boolean containsSkill(String text, String skill) {
        if (skill.equals("c")) {
            return Pattern.compile("\\b(c language|programming in c|c/c\\+\\+)\\b").matcher(text).find();
        }
        if (skill.equals("r")) {
            return Pattern.compile("\\b(r language|programming in r)\\b").matcher(text).find();
        }
        if (skill.equals("gcp")) {
            return Pattern.compile("\\b(gcp|google cloud platform)\\b").matcher(text).find();
        }
        if (skill.equals("oop")) {
            return Pattern.compile("\\b(oop|object oriented programming)\\b").matcher(text).find();
        }
        return Pattern.compile("\\b" + Pattern.quote(skill) + "\\b").matcher(text).find();
    }

    private boolean containsPattern(String text, String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).find();
    }

    // JSON serialization helpers
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }

    public <T> T fromJson(String json, TypeReference<T> typeReference, T defaultValue) {
        if (json == null || json.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
