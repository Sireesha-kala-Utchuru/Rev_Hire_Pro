# Rev_Hire_pro (Java + JDBC + MySQL + Log4j2 + JUnit)

A console-based **Job Portal** that connects **Job Seekers** and **Employers**.

## Features

### Job Seeker
- Register & Login
- Update Profile (location, experience years, skills)
- Create/Update **text resume** (structured)
- Search Jobs (filters: role/title, location, experience, company)
- Apply for Job (using saved resume + optional cover letter)
- View Application Status
- Withdraw Application
- Notifications (view + mark read)
- Change Password + Forgot Password (security questions)

### Employer
- Register & Login
- Create/Update Company Profile
- Create Job Posting
- View My Job Postings
- Open/Close Job
- View Applicants for a Job
- Shortlist/Reject Applications
- Notifications (view + mark read)
- Change Password

## Architecture (Layered Design)
- **UI Layer**: `ConsoleApp.java` (menus + input, no DB logic)
- **Service Layer**: business rules + validations + transactions
- **DAO Layer**: JDBC + SQL only (PreparedStatement, ResultSet mapping)
- **MySQL**: tables + foreign keys
- Cross-cutting: Log4j2, AppConfig, Jdbc utility, JUnit tests

Diagrams:
- `docs/Rev_Hire_pro_Architecture.png`
- `docs/Rev_Hire_pro_ERD.png`

## Setup (MySQL)

1) Create DB
```sql
CREATE DATABASE IF NOT EXISTS rev_hire_pro;
```

2) Run schema
- Open `db/schema.sql` and execute inside the `rev_hire_pro` database.

3) Update DB credentials
- `src/main/resources/application.properties`

## Run

### IntelliJ
Run the main class:
- `com.revhirepro.App`

### Maven
```bash
mvn clean package
mvn -q exec:java -Dexec.mainClass=com.revhirepro.App
```

## Testing

```bash
mvn clean test
```

Coverage report:
- `target/site/jacoco/index.html`
