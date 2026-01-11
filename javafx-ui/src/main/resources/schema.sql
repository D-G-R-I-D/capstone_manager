CREATE DATABASE IF NOT EXISTS capstone_manager;
USE capstone_manager;

-- USERS TABLE
CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'SUPERVISOR', 'SENIOR_SUPERVISOR', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE milestones (
    id VARCHAR(36) PRIMARY KEY,
    project_id VARCHAR(36),
    title VARCHAR(255),
    deadline DATE,
    status ENUM('NOT_STARTED','IN_PROGRESS','COMPLETED') DEFAULT 'NOT_STARTED',
    FOREIGN KEY (project_id) REFERENCES projects(id)
);


-- ============================
-- COMMENTS TABLE
-- (two-level: main comment + replies)
-- ============================
CREATE TABLE comments (
    id VARCHAR(36) PRIMARY KEY,
    project_id VARCHAR(36),
    author_id VARCHAR(36),
    parent_id VARCHAR(36) NULL,        -- NULL for main comments
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
);


-- ============================
-- SCORECARDS TABLE
-- supervisor + senior separate
-- ============================
CREATE TABLE scorecards (
    id VARCHAR(36) PRIMARY KEY,
    project_id VARCHAR(36),
    graded_by VARCHAR(36),
    role ENUM('SUPERVISOR','SENIOR_SUPERVISOR','ADMIN'),
    override BOOLEAN DEFAULT FALSE,

    technical_depth INT,
    problem_solving INT,
    presentation INT,
    design INT,

    total_score INT,
    graded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (graded_by) REFERENCES users(id)
);

-- PROJECTS TABLE
CREATE TABLE projects (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(255),
    student_id VARCHAR(50),
    supervisor_id VARCHAR(50),
    file_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'APPROVED', 'COMPLETED', 'ON_HOLD', 'REJECTED') DEFAULT 'ACTIVE',

    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (supervisor_id) REFERENCES users(id)
);

-- PROPOSALS TABLE
CREATE TABLE proposals (
    id VARCHAR(50) PRIMARY KEY,
    project_id VARCHAR(50),
    summary TEXT,
    submitted_by VARCHAR(50),
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    reviewed_by VARCHAR(50),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    file_data LONGBLOB,

    FOREIGN KEY (submitted_by) REFERENCES users(id),
    FOREIGN KEY (reviewed_by) REFERENCES users(id),
    approved_by_supervisor BOOLEAN DEFAULT FALSE,
    approved_by_senior BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (project_id) REFERENCES projects(id)
)  ENGINE=InnoDB;


--logs table (for admin monitoring everything)
-- ============================
-- LOGS TABLE
-- ============================
CREATE TABLE logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    action TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
)ENGINE=InnoDB;



--settings table (system configuration controlled by admin)
CREATE TABLE settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value TEXT
);

CREATE TABLE logs (
     id INT AUTO_INCREMENT PRIMARY KEY,
     user_id VARCHAR(50),
     action TEXT NOT NULL,
     timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (user_id) REFERENCES users(id)
 )ENGINE=InnoDB;


