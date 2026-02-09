package com.revhirepro.util;

public final class ResumeUtil {
    private ResumeUtil() {}

    public static String build(String objective, String education, String experience, String skills, String projects) {
        return "OBJECTIVE:\n" + safe(objective) + "\n\n" +
               "EDUCATION:\n" + safe(education) + "\n\n" +
               "EXPERIENCE:\n" + safe(experience) + "\n\n" +
               "SKILLS:\n" + safe(skills) + "\n\n" +
               "PROJECTS:\n" + safe(projects) + "\n";
    }

    private static String safe(String text) {
        return text == null ? "" : text.trim();
    }
}
