package com.revhirepro.ui;

import com.revhirepro.model.Role;
import com.revhirepro.model.User;
import com.revhirepro.service.ApplicationService;
import com.revhirepro.service.AuthService;
import com.revhirepro.service.EmployerService;
import com.revhirepro.service.JobSeekerService;
import com.revhirepro.service.JobService;
import com.revhirepro.service.NotificationService;
import com.revhirepro.util.ResumeUtil;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Scanner;

public class ConsoleApp {

    private final AuthService authService = new AuthService();
    private final JobSeekerService jobSeekerService = new JobSeekerService();
    private final EmployerService employerService = new EmployerService();
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();
    private final NotificationService notificationService = new NotificationService();

    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("=== Welcome to Rev_hire_pro (Console Job Portal) ===");
        while (true) {
            System.out.println("1) Register Job Seeker");
            System.out.println("2) Register Employer");
            System.out.println("3) Login");
            System.out.println("4) Forgot Password");
            System.out.println("5) Exit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> register(Role.JOB_SEEKER);
                case "2" -> register(Role.EMPLOYER);
                case "3" -> {
                    User user = login();
                    if (user != null) home(user);
                }
                case "4" -> forgotPassword();
                case "5" -> { System.out.println("Bye!"); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void register(Role role) {
        System.out.println("--- Registration (" + role + ") ---");
        String fullName = ask("Full name");
        String email = ask("Email");
        String phone = ask("Phone");
        String password = ask("Password");

        System.out.println("Choose 2 security questions (for password reset). Available questions:");
        authService.showQuestions();
        int question1Id = askInt("Question 1 id");
        String answer1 = ask("Answer 1");
        int question2Id = askInt("Question 2 id");
        String answer2 = ask("Answer 2");

        User user = authService.register(role, fullName, email, phone, password, question1Id, answer1, question2Id, answer2);
        if (user == null) return;

        if (role == Role.JOB_SEEKER) {
            jobSeekerService.createEmptyProfile(user.getId());
            System.out.println("Job seeker profile initialized.");
        } else {
            System.out.println("Now create company profile after login (recommended).");
        }
    }

    private User login() {
        System.out.println("--- Login ---");
        String login = ask("Email/Phone");
        String password = ask("Password");
        return authService.login(login, password);
    }

    private void forgotPassword() {
        System.out.println("--- Forgot Password ---");
        String login = ask("Email/Phone");
        System.out.println("Answer your security questions:");
        authService.showQuestions();
        int question1Id = askInt("Question 1 id");
        String answer1 = ask("Answer 1");
        int question2Id = askInt("Question 2 id");
        String answer2 = ask("Answer 2");
        String newPassword = ask("New password");
        authService.resetPasswordWithSecurity(login, question1Id, answer1, question2Id, answer2, newPassword);
    }

    private void home(User user) {
        if (user.getRole() == Role.JOB_SEEKER) jobSeekerHome(user);
        else employerHome(user);
    }

    private void jobSeekerHome(User user) {
        System.out.println("=== Job Seeker Home: " + user.getName() + " ===");
        while (true) {
            System.out.println();
            System.out.println("1) View Profile + Resume");
            System.out.println("2) Update Profile (location, exp years, skills)");
            System.out.println("3) Create/Update Resume (structured text)");
            System.out.println("4) Search Jobs");
            System.out.println("5) Apply for Job");
            System.out.println("6) My Applications");
            System.out.println("7) Withdraw Application");
            System.out.println("8) Notifications");
            System.out.println("9) Mark Notifications Read");
            System.out.println("10) Change Password");
            System.out.println("11) Logout");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> jobSeekerService.viewProfile(user.getId());
                case "2" -> updateJobSeekerProfile(user);
                case "3" -> updateResume(user);
                case "4" -> searchJobs();
                case "5" -> applyJob(user);
                case "6" -> applicationService.myApplications(user.getId());
                case "7" -> withdraw(user);
                case "8" -> notificationService.listRecent(user.getId());
                case "9" -> notificationService.markAllRead(user.getId());
                case "10" -> changePassword(user);
                case "11" -> { System.out.println("Logged out."); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void employerHome(User user) {
        System.out.println("=== Employer Home: " + user.getName() + " ===");
        while (true) {
            System.out.println();
            System.out.println("1) View Company Profile");
            System.out.println("2) Create/Update Company Profile");
            System.out.println("3) Create Job Posting");
            System.out.println("4) View My Job Postings");
            System.out.println("5) Close Job");
            System.out.println("6) Reopen Job");
            System.out.println("7) View Applicants For Job");
            System.out.println("8) Shortlist Application");
            System.out.println("9) Reject Application");
            System.out.println("10) Notifications");
            System.out.println("11) Mark Notifications Read");
            System.out.println("12) Change Password");
            System.out.println("13) Logout");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> employerService.viewCompany(user.getId());
                case "2" -> upsertCompany(user);
                case "3" -> createJob(user);
                case "4" -> jobService.listMyJobs(user.getId());
                case "5" -> jobService.closeJob(user.getId(), askLong("jobId"));
                case "6" -> jobService.openJob(user.getId(), askLong("jobId"));
                case "7" -> applicationService.applicantsForJob(user.getId(), askLong("jobId"));
                case "8" -> applicationService.updateStatus(user.getId(), askLong("applicationId"), "SHORTLISTED");
                case "9" -> applicationService.updateStatus(user.getId(), askLong("applicationId"), "REJECTED");
                case "10" -> notificationService.listRecent(user.getId());
                case "11" -> notificationService.markAllRead(user.getId());
                case "12" -> changePassword(user);
                case "13" -> { System.out.println("Logged out."); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void updateJobSeekerProfile(User user) {
        System.out.println("--- Update Profile ---");
        String location = ask("Location");
        int experienceYears = askInt("Experience years");
        String skills = ask("Skills (comma separated)");
        jobSeekerService.updateProfile(user.getId(), location, experienceYears, skills);
    }

    private void updateResume(User user) {
        System.out.println("--- Resume Builder (text) ---");
        String objective = askMultiline("Objective (type END on new line to finish)");
        String education = askMultiline("Education (type END)");
        String experience = askMultiline("Experience (type END)");
        String skills = askMultiline("Skills (type END)");
        String projects = askMultiline("Projects (type END)");
        String resume = ResumeUtil.build(objective, education, experience, skills, projects);
        jobSeekerService.saveResume(user.getId(), resume);
    }

    private void searchJobs() {
        System.out.println("--- Search Jobs ---");
        String roleTitle = askOptional("Job role/title (optional)");
        String location = askOptional("Location (optional)");
        Integer exp = askOptionalInt("Experience years (optional)");
        String company = askOptional("Company name (optional)");
        Integer salaryMin = askOptionalInt("Minimum salary expectation (optional)");
        String jobType = askOptional("Job type (FULL_TIME/PART_TIME/INTERN/CONTRACT) optional");
        jobService.searchJobs(roleTitle, location, exp, company, salaryMin, jobType);
    }

    private void applyJob(User user) {
        System.out.println("--- Apply ---");
        long jobId = askLong("jobId");
        if (!jobService.jobIsOpen(jobId)) {
            System.out.println("Job is not open or not found.");
            return;
        }
        String coverLetter = askOptional("Cover letter (optional)");
        applicationService.apply(jobId, user.getId(), coverLetter);
    }

    private void withdraw(User user) {
        System.out.println("--- Withdraw Application ---");
        long applicationId = askLong("applicationId");
        applicationService.withdraw(applicationId, user.getId());
    }

    private void upsertCompany(User user) {
        System.out.println("--- Company Profile ---");
        String name = ask("Company name");
        String industry = askOptional("Industry");
        String size = askOptional("Company size");
        String desc = askOptional("Description");
        String website = askOptional("Website");
        String loc = askOptional("Location");
        employerService.createCompany(user.getId(), name, industry, size, desc, website, loc);
        System.out.println("If company already exists, use UPDATE by rerun after deleting row or update in DB (simple demo).");
    }

    private void createJob(User user) {
        System.out.println("--- Create Job ---");
        String title = ask("Title");
        String desc = askMultiline("Description (type END)");
        String skills = askOptional("Skills (comma separated)");
        int minExp = askInt("Min experience years");
        String education = askOptional("Education (optional)");
        String location = askOptional("Location (optional)");
        Integer salMin = askOptionalInt("Salary min (optional)");
        Integer salMax = askOptionalInt("Salary max (optional)");
        String type = ask("Job type (FULL_TIME/PART_TIME/INTERN/CONTRACT)");
        String dl = askOptional("Deadline (YYYY-MM-DD) optional");

        Date deadline = null;
        if (dl != null && !dl.isBlank()) {
            deadline = Date.valueOf(LocalDate.parse(dl.trim()));
        }

        jobService.createJob(user.getId(), title, desc, skills, minExp, education, location, salMin, salMax, type, deadline);
    }

    private void changePassword(User user) {
        System.out.println("--- Change Password ---");
        String currentPassword = ask("Current password");
        String newPassword = ask("New password");
        authService.changePassword(user.getId(), currentPassword, newPassword);
    }

    private String ask(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    private String askOptional(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    private Integer askOptionalInt(String label) {
        String value = askOptional(label);
        if (value == null || value.isBlank()) return null;
        try { return Integer.parseInt(value.trim()); } catch (Exception ex) { return null; }
    }

    private int askInt(String label) {
        while (true) {
            String value = ask(label);
            try { return Integer.parseInt(value); } catch (Exception ex) { System.out.println("Enter valid integer."); }
        }
    }

    private long askLong(String label) {
        while (true) {
            String value = ask(label);
            try { return Long.parseLong(value); } catch (Exception ex) { System.out.println("Enter valid number."); }
        }
    }

    private String askMultiline(String label) {
        System.out.println(label + ":");
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if ("END".equalsIgnoreCase(line.trim())) break;
            sb.append(line).append("\n");
        }
        return sb.toString().trim();
    }
}
