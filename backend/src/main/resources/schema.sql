-- ═══════════════════════════════════════════════════════
-- T-MAT Global LIMS — FINAL CLEAN SCHEMA (MATCHED)
-- ═══════════════════════════════════════════════════════

-- ROLES
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- USERS
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(80) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(80),
    last_name VARCHAR(80),
    email VARCHAR(150) UNIQUE,
    mobile VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- USER ROLES
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- PATIENTS
CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    salutation VARCHAR(10),
    name VARCHAR(150) NOT NULL,
    gender VARCHAR(10),
    age INT,
    age_unit VARCHAR(10),
    date_of_birth DATE,
    mobile VARCHAR(20),
    alternate_mobile VARCHAR(20),
    email VARCHAR(150),
    address TEXT,
    remarks TEXT,
    passport_no VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- REFERRING DOCTORS
CREATE TABLE referring_doctors (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(150) NOT NULL,
    mobile VARCHAR(20),
    email VARCHAR(150),
    address TEXT,
    city VARCHAR(100),
    patient_type VARCHAR(20),
    rate_type VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- TESTS
CREATE TABLE tests (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(20),
    department VARCHAR(100),
    rate DECIMAL(10,2),
    description TEXT,
    sample_type VARCHAR(100),
    turnaround_hours INT,
    is_active BOOLEAN DEFAULT TRUE,
    parameters TEXT,
    ranges TEXT,
    test_config TEXT,
    formula TEXT,
    report_notes TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- PARAMETERS
CREATE TABLE parameters (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) UNIQUE NOT NULL,
    code VARCHAR(50),
    default_result VARCHAR(100),
    order_num INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- REGISTRATIONS
CREATE TABLE registrations (
    id BIGSERIAL PRIMARY KEY,
    reg_no VARCHAR(50) UNIQUE NOT NULL,
    patient_id BIGINT REFERENCES patients(id),
    ref_doctor_id BIGINT REFERENCES referring_doctors(id),
    patient_type VARCHAR(20),
    center VARCHAR(100),
    payment_type VARCHAR(20),
    total_amount DECIMAL(12,2),
    other_charges DECIMAL(10,2),
    discount_amount DECIMAL(10,2),
    discount_type VARCHAR(10),
    net_amount DECIMAL(12,2),
    paid_amount DECIMAL(12,2),
    balance_amount DECIMAL(12,2),
    remarks TEXT,
    notify_on_lab BOOLEAN,
    notify_email BOOLEAN,
    notify_whatsapp BOOLEAN,
    is_emergency BOOLEAN,
    status VARCHAR(30),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- REGISTRATION TESTS
CREATE TABLE registration_tests (
    id BIGSERIAL PRIMARY KEY,
    registration_id BIGINT REFERENCES registrations(id) ON DELETE CASCADE,
    test_id BIGINT REFERENCES tests(id),
    rate DECIMAL(10,2),
    client_rate DECIMAL(10,2),
    status VARCHAR(30),
    result_value TEXT,
    result_unit VARCHAR(50),
    reference_range VARCHAR(200),
    is_abnormal BOOLEAN,
    tested_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- AUDIT LOGS
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100),
    action VARCHAR(100),
    entity_type VARCHAR(100),
    entity_id BIGINT,
    description TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP
);