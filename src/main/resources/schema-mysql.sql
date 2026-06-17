-- ============================================================
-- ID Card Management System — SQL Schema
-- Package: net.orderzone.idcard
-- Database: id_card_db (MySQL 8+)
-- ============================================================



-- ── Templates (must exist before profiles FK) ────────────────
CREATE TABLE IF NOT EXISTS templates (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    code              VARCHAR(60)  NOT NULL,
    name              VARCHAR(80)  NOT NULL,
    organization_name VARCHAR(120),
    layout            VARCHAR(20)  NOT NULL DEFAULT 'VERTICAL',
    primary_color     VARCHAR(7)   NOT NULL DEFAULT '#1d4ed8',
    secondary_color   VARCHAR(7)   NOT NULL DEFAULT '#e0e7ff',
    text_color        VARCHAR(7)   NOT NULL DEFAULT '#111827',
    tagline           VARCHAR(255),

    PRIMARY KEY (id),
    UNIQUE KEY uk_templates_code (code),
    UNIQUE KEY uk_templates_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Profiles ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS profiles (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    uuid                VARCHAR(36)  NOT NULL,
    registration_number VARCHAR(64)  NOT NULL,
    type                VARCHAR(16)  NOT NULL,
    full_name           VARCHAR(120) NOT NULL,
    department          VARCHAR(80),
    title               VARCHAR(120),
    email               VARCHAR(120),
    phone               VARCHAR(40)  NOT NULL,
    blood_group         VARCHAR(60),
    date_of_birth       DATE,
    issue_date          DATE,
    expiry_date         DATE,
    photo_file_name     VARCHAR(255),
    photo_content_type  VARCHAR(60),
    template_id         BIGINT,
    barcode_type        VARCHAR(16)  DEFAULT 'CODE_128',
    created_at          DATETIME(6)  NOT NULL,
    updated_at          DATETIME(6)  NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_profile_uuid      (uuid),
    UNIQUE KEY uk_profile_reg_number (registration_number),
    UNIQUE KEY uk_profile_email     (email),
    INDEX idx_profiles_type         (type),
    INDEX idx_profiles_full_name    (full_name),
    CONSTRAINT fk_profiles_template
        FOREIGN KEY (template_id) REFERENCES templates (id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Seed: Default Templates ───────────────────────────────────
INSERT INTO templates (code, name, organization_name, layout,
                       primary_color, secondary_color, text_color, tagline)
VALUES
    ('DEFAULT',    'Default Blue',     'ID Card System',       'VERTICAL',   '#1d4ed8', '#e0e7ff', '#111827', 'Official Identification Card'),
    ('DARK_NAVY',  'Dark Navy',        'ID Card System',       'VERTICAL',   '#0f3460', '#e8f4f8', '#ffffff', 'Authorized Personnel Only'),
    ('GREEN',      'Emerald Green',    'ID Card System',       'HORIZONTAL', '#065f46', '#d1fae5', '#111827', 'Go Green Initiative'),
    ('PURPLE',     'Royal Purple',     'ID Card System',       'VERTICAL',   '#6d28d9', '#ede9fe', '#111827', 'Excellence & Innovation')
ON DUPLICATE KEY UPDATE code = code;

-- ── Seed: Sample Profiles ─────────────────────────────────────
INSERT INTO profiles (uuid, registration_number, type, full_name, department,
                      title, email, phone, blood_group, date_of_birth,
                      issue_date, expiry_date, barcode_type, created_at, updated_at)
VALUES
    (UUID(), '2026-STU-001', 'STUDENT',  'Alice Johnson',
     'Computer Science', 'Bachelor Year 3',
     'alice@university.edu', '+855-12-111-001', 'O+',
     '2002-05-15', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 4 YEAR),
     'CODE_128', NOW(), NOW()),

    (UUID(), '2026-EMP-001', 'EMPLOYEE', 'Bob Smith',
     'Engineering', 'Senior Developer',
     'bob@company.com', '+855-23-222-002', 'A+',
     '1988-08-22', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 YEAR),
     'CODE_128', NOW(), NOW()),

    (UUID(), '2026-USR-001', 'USER', 'Carol White',
     NULL, 'Member',
     'carol@email.com', '+855-34-333-003', 'B-',
     '1995-11-30', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR),
     'EAN_13', NOW(), NOW())
ON DUPLICATE KEY UPDATE registration_number = registration_number;
