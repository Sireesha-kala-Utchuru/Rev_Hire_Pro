SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS rev_hire_pro;
CREATE DATABASE rev_hire_pro;
USE rev_hire_pro;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE rh_users (
  user_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  role ENUM('JOB_SEEKER','EMPLOYER') NOT NULL,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  phone VARCHAR(30) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  is_locked BOOLEAN NOT NULL DEFAULT FALSE,
  failed_attempts INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE rh_security_questions (
  question_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  question_text VARCHAR(200) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE rh_user_security_answers (
  user_id BIGINT UNSIGNED NOT NULL,
  question_id INT UNSIGNED NOT NULL,
  answer_hash VARCHAR(100) NOT NULL,
  PRIMARY KEY(user_id, question_id),
  CONSTRAINT fk_usa_user FOREIGN KEY(user_id) REFERENCES rh_users(user_id),
  CONSTRAINT fk_usa_q FOREIGN KEY(question_id) REFERENCES rh_security_questions(question_id)
) ENGINE=InnoDB;

CREATE TABLE rh_job_seeker_profiles (
  user_id BIGINT UNSIGNED PRIMARY KEY,
  location VARCHAR(120),
  experience_years INT DEFAULT 0,
  skills TEXT,
  resume_text LONGTEXT,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_jsp_user FOREIGN KEY(user_id) REFERENCES rh_users(user_id)
) ENGINE=InnoDB;

CREATE TABLE rh_employer_companies (
  user_id BIGINT UNSIGNED PRIMARY KEY,
  company_name VARCHAR(150) NOT NULL,
  industry VARCHAR(120),
  company_size VARCHAR(50),
  description TEXT,
  website VARCHAR(150),
  location VARCHAR(120),
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_ec_user FOREIGN KEY(user_id) REFERENCES rh_users(user_id)
) ENGINE=InnoDB;

CREATE TABLE rh_jobs (
  job_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  employer_user_id BIGINT UNSIGNED NOT NULL,
  title VARCHAR(150) NOT NULL,
  description TEXT NOT NULL,
  skills TEXT,
  min_experience_years INT DEFAULT 0,
  education VARCHAR(120),
  location VARCHAR(120),
  salary_min INT,
  salary_max INT,
  job_type ENUM('FULL_TIME','PART_TIME','INTERN','CONTRACT') NOT NULL,
  deadline DATE,
  status ENUM('OPEN','CLOSED') NOT NULL DEFAULT 'OPEN',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_job_emp FOREIGN KEY(employer_user_id) REFERENCES rh_users(user_id)
) ENGINE=InnoDB;

CREATE TABLE rh_applications (
  application_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  job_id BIGINT UNSIGNED NOT NULL,
  job_seeker_user_id BIGINT UNSIGNED NOT NULL,
  cover_letter TEXT,
  status ENUM('APPLIED','SHORTLISTED','REJECTED','WITHDRAWN') NOT NULL DEFAULT 'APPLIED',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_app_job FOREIGN KEY(job_id) REFERENCES rh_jobs(job_id),
  CONSTRAINT fk_app_js FOREIGN KEY(job_seeker_user_id) REFERENCES rh_users(user_id),
  UNIQUE KEY uniq_job_user (job_id, job_seeker_user_id)
) ENGINE=InnoDB;

CREATE TABLE rh_notifications (
  notification_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  category ENUM('APPLICATION','JOB_MATCH','SYSTEM') NOT NULL,
  message VARCHAR(400) NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_n_user FOREIGN KEY(user_id) REFERENCES rh_users(user_id)
) ENGINE=InnoDB;

INSERT INTO rh_security_questions(question_id, question_text) VALUES
  (1, 'What is your favorite color?'),
  (2, 'What is your first school name?'),
  (3, 'What is your pet name?');

SHOW TABLES;
